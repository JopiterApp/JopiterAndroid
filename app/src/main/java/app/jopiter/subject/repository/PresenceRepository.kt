/*
* Jopiter App
* Copyright (C) 2026 Leonardo Colman Lopes
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
import app.jopiter.PresenceQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/** Stores the dates a student attended a subject's classes (its "presences"). */
class PresenceRepository(private val queries: PresenceQueries) {

  @Suppress("InjectDispatcher")
  val attendanceBySubject: Flow<Map<Long, Set<LocalDate>>> =
    queries.selectAll().asFlow().mapToList(Dispatchers.IO).map { rows ->
      rows.groupBy({ it.subjectId }, { LocalDate.parse(it.date) }).mapValues { (_, dates) -> dates.toSet() }
    }

  fun isPresent(subjectId: Long, date: LocalDate): Boolean =
    queries.isPresent(subjectId, date.toString()).executeAsOne() > 0

  fun setPresent(subjectId: Long, date: LocalDate, present: Boolean) {
    if (present) queries.insert(subjectId, date.toString()) else queries.delete(subjectId, date.toString())
  }

  fun togglePresent(subjectId: Long, date: LocalDate) =
    setPresent(subjectId, date, present = !isPresent(subjectId, date))
}
