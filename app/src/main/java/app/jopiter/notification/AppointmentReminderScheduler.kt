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
@file:Suppress("MagicNumber")

package app.jopiter.notification

import app.jopiter.calendar.model.Appointment
import java.time.LocalDateTime

/**
 * Pure scheduling logic for appointment reminders. Mirrors the legacy behaviour: warn 30, 15 and
 * 7-down-to-1 days before the appointment (at the same time of day), plus a same-day nudge 12 hours
 * before it. Only reminders still in the future (relative to [now]) are produced. No Android deps.
 */
object AppointmentReminderScheduler {

  private val DAY_OFFSETS = listOf(30, 15, 7, 6, 5, 4, 3, 2, 1)
  private const val SAME_DAY_HOURS_BEFORE = 12L
  const val SAME_DAY = 0

  fun remindersFor(
    appointment: Appointment,
    now: LocalDateTime,
    subjectName: String? = null
  ): List<AppointmentReminder> {
    val triggers = DAY_OFFSETS.map { days -> days to appointment.dateTime.minusDays(days.toLong()) } +
      (SAME_DAY to appointment.dateTime.minusHours(SAME_DAY_HOURS_BEFORE))

    return triggers
      .filter { (_, triggerAt) -> triggerAt.isAfter(now) }
      .map { (daysLeft, triggerAt) ->
        AppointmentReminder(appointment.id, appointment.name, subjectName, daysLeft, triggerAt)
      }
  }
}
