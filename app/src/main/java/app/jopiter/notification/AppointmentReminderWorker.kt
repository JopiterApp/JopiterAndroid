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

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDateTime

/**
 * Posts a single appointment reminder ("N days left"). One-shot: each of an appointment's reminders
 * is enqueued as its own worker. All data comes from [inputData].
 */
class AppointmentReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    val reminder = AppointmentReminder(
      appointmentId = inputData.getLong(KEY_APPOINTMENT_ID, 0),
      appointmentName = inputData.getString(KEY_APPOINTMENT_NAME).orEmpty(),
      subjectName = inputData.getString(KEY_SUBJECT_NAME),
      daysLeft = inputData.getInt(KEY_DAYS_LEFT, 0),
      triggerAt = LocalDateTime.now()
    )

    ReminderNotifications.notifyAppointment(applicationContext, reminder)

    return Result.success()
  }

  companion object {
    const val KEY_APPOINTMENT_ID = "appointment_id"
    const val KEY_APPOINTMENT_NAME = "appointment_name"
    const val KEY_SUBJECT_NAME = "subject_name"
    const val KEY_DAYS_LEFT = "days_left"
  }
}
