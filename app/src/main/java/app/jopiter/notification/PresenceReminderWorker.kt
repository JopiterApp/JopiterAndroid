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
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Posts a presence reminder when a class starts, then re-enqueues itself for the same class next
 * week so the reminder recurs even if the app is never reopened. All data comes from [inputData];
 * the worker touches no repository.
 */
class PresenceReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    val startAt = inputData.getString(KEY_START_AT)?.let(LocalTime::parse) ?: return Result.failure()
    val reminder = PresenceReminder(
      subjectId = inputData.getLong(KEY_SUBJECT_ID, 0),
      subjectName = inputData.getString(KEY_SUBJECT_NAME).orEmpty(),
      dayOfWeek = DayOfWeek.of(inputData.getInt(KEY_DAY_OF_WEEK, DayOfWeek.MONDAY.value)),
      startAt = startAt,
      triggerAt = LocalDateTime.now()
    )

    ReminderNotifications.notifyPresence(applicationContext, reminder)

    val next = PresenceReminderScheduler.nextOccurrence(reminder.dayOfWeek, reminder.startAt, LocalDateTime.now())
    ReminderScheduler(applicationContext).enqueuePresence(reminder.copy(triggerAt = next))

    return Result.success()
  }

  companion object {
    const val KEY_SUBJECT_ID = "subject_id"
    const val KEY_SUBJECT_NAME = "subject_name"
    const val KEY_DAY_OF_WEEK = "day_of_week"
    const val KEY_START_AT = "start_at"
  }
}
