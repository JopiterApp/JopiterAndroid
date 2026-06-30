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
package app.jopiter.calendar.model

import java.time.LocalDateTime

/** A dated event (exam, assignment, homework or other), optionally tied to a subject. */
data class Appointment(
  val id: Long = 0,
  val subjectId: Long? = null,
  val name: String,
  val description: String = "",
  val dateTime: LocalDateTime,
  val type: AppointmentType
)

enum class AppointmentType { Exam, Assignment, Homework, Other }
