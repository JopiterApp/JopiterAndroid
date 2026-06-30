/*
* Jopiter App
* Copyright (C) 2022 Leonardo Colman Lopes
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package app.jopiter.subject.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.jopiter.ClassTimeQueries
import app.jopiter.SubjectQueries
import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import app.jopiter.ClassTime as ClassTimeRow
import app.jopiter.Subject as SubjectRow

/**
 * Persists [Subject]s and their [ClassTime]s. Class times are always stored as a full replacement
 * of a subject's set, so saving and deleting keep the two tables consistent within a transaction.
 */
class SubjectRepository(
  private val subjectQueries: SubjectQueries,
  private val classTimeQueries: ClassTimeQueries
) {

  @Suppress("InjectDispatcher")
  val subjects: Flow<List<Subject>> = combine(
    subjectQueries.selectAll().asFlow().mapToList(Dispatchers.IO),
    classTimeQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
  ) { subjectRows, classTimeRows ->
    val classTimesBySubject = classTimeRows.groupBy { it.subjectId }
    subjectRows.map { it.toSubject(classTimesBySubject[it.id].orEmpty()) }
  }

  /** Inserts (when [Subject.id] is 0) or updates the subject and replaces its class times. Returns the id. */
  fun save(subject: Subject): Long = subjectQueries.transactionWithResult {
    val id = if (subject.id == 0L) {
      subjectQueries.insert(
        name = subject.name,
        code = subject.code,
        classroom = subject.classroom,
        lecturer = subject.lecturer,
        lecturerEmail = subject.lecturerEmail,
        maxMissedClasses = subject.maxMissedClasses.toLong(),
        creationDate = subject.creationDate.toString()
      )
      subjectQueries.lastInsertedId().executeAsOne()
    } else {
      subjectQueries.update(
        name = subject.name,
        code = subject.code,
        classroom = subject.classroom,
        lecturer = subject.lecturer,
        lecturerEmail = subject.lecturerEmail,
        maxMissedClasses = subject.maxMissedClasses.toLong(),
        id = subject.id
      )
      subject.id
    }
    classTimeQueries.deleteBySubject(id)
    subject.classTimes.forEach { classTime ->
      classTimeQueries.insert(id, classTime.dayOfWeek.name, classTime.startAt.toString(), classTime.endAt.toString())
    }
    id
  }

  /** Loads a single subject (with its class times) by id, or null if it does not exist. */
  fun findById(id: Long): Subject? {
    val row = subjectQueries.selectById(id).executeAsOneOrNull() ?: return null
    return row.toSubject(classTimeQueries.selectBySubject(id).executeAsList())
  }

  fun delete(id: Long) = subjectQueries.transaction {
    classTimeQueries.deleteBySubject(id)
    subjectQueries.deleteById(id)
  }

  /** Whether another subject (id != [excludingId]) already uses [name]. */
  fun isNameTaken(name: String, excludingId: Long): Boolean =
    subjectQueries.countByNameExcludingId(name, excludingId).executeAsOne() > 0
}

private fun SubjectRow.toSubject(classTimes: List<ClassTimeRow>) = Subject(
  id = id,
  name = name,
  code = code,
  classroom = classroom,
  lecturer = lecturer,
  lecturerEmail = lecturerEmail,
  maxMissedClasses = maxMissedClasses.toInt(),
  creationDate = LocalDate.parse(creationDate),
  classTimes = classTimes.map { it.toClassTime() }
)

private fun ClassTimeRow.toClassTime() = ClassTime(
  dayOfWeek = DayOfWeek.valueOf(dayOfWeek),
  startAt = LocalTime.parse(startAt),
  endAt = LocalTime.parse(endAt)
)
