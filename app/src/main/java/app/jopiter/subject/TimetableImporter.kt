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
package app.jopiter.subject

import app.jopiter.subject.external.TimetableEntry
import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TimetableTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

/**
 * Pure mapping from a JupiterWeb timetable response into persistable [Subject]s.
 *
 * A subject taught on several weekdays arrives as one [TimetableEntry] per day; entries are grouped
 * by subject code (falling back to its name) so each becomes a single [Subject] holding every weekly
 * [ClassTime]. Class times are sorted by day then start time for a stable display order.
 */
object TimetableImporter {

  fun toSubjects(
    timetable: Map<DayOfWeek, List<TimetableEntry>>,
    creationDate: LocalDate = LocalDate.now()
  ): List<Subject> {
    val bySubject = timetable.entries
      .flatMap { (day, entries) -> entries.map { day to it } }
      .groupBy { (_, entry) -> entry.subject.code.ifBlank { entry.information.name } }

    return bySubject.map { (key, dayEntries) ->
      val sample = dayEntries.first().second
      Subject(
        name = sample.information.name.ifBlank { key },
        code = sample.subject.code,
        creationDate = creationDate,
        classTimes = dayEntries
          .map { (day, entry) -> ClassTime(day, parse(entry.subject.startTime), parse(entry.subject.endTime)) }
          .sortedWith(compareBy({ it.dayOfWeek }, { it.startAt }))
      )
    }
  }

  private fun parse(time: String): LocalTime = LocalTime.parse(time, TimetableTimeFormat)
}
