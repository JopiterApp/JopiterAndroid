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
package app.jopiter.subject.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * A subject the student is enrolled in, with the weekly times its classes happen.
 * [id] is 0 for a subject that has not been persisted yet.
 */
data class Subject(
  val id: Long = 0,
  val name: String,
  val code: String = "",
  val classroom: String = "",
  val lecturer: String = "",
  val lecturerEmail: String = "",
  val maxMissedClasses: Int = 0,
  val creationDate: LocalDate = LocalDate.now(),
  val classTimes: List<ClassTime> = emptyList()
)

/** A weekly recurring class slot for a subject. */
data class ClassTime(
  val dayOfWeek: DayOfWeek,
  val startAt: LocalTime,
  val endAt: LocalTime
)
