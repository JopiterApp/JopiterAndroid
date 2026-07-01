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
package app.jopiter.notification

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

/** A reminder to confirm attendance, due when one of a subject's weekly classes starts. */
data class PresenceReminder(
  val subjectId: Long,
  val subjectName: String,
  val dayOfWeek: DayOfWeek,
  val startAt: LocalTime,
  val triggerAt: LocalDateTime
) {
  /** Stable key identifying this recurring class slot, used as the WorkManager unique-work name. */
  val uniqueName: String get() = "presence:$subjectId:${dayOfWeek.value}:$startAt"
}

/** A reminder that an appointment is [daysLeft] days away (0 meaning it is later today). */
data class AppointmentReminder(
  val appointmentId: Long,
  val appointmentName: String,
  val subjectName: String?,
  val daysLeft: Int,
  val triggerAt: LocalDateTime
) {
  val uniqueName: String get() = "appointment:$appointmentId:$daysLeft"
}
