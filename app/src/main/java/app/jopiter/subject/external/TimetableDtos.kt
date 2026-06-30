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
package app.jopiter.subject.external

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Wire shape of one class in a JupiterWeb timetable, as returned by the timetable-fetcher backend.
 * The endpoint keys these entries by [java.time.DayOfWeek] name (e.g. `"MONDAY"`). Times are raw
 * `HH:mm:ss` strings here and parsed by [app.jopiter.subject.TimetableImporter].
 *
 * Defaults plus lenient deserialization make parsing resilient to an experimental, unversioned API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TimetableEntry(
  val subject: TimetableSubject = TimetableSubject(),
  val information: TimetableInformation = TimetableInformation()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimetableSubject(
  val code: String = "",
  val classCode: String = "",
  val startTime: String = "",
  val endTime: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimetableInformation(
  val name: String = ""
)
