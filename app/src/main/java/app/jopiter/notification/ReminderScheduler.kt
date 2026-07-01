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
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import app.jopiter.calendar.model.Appointment
import app.jopiter.subject.model.Subject
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.math.max

/**
 * Turns the pure reminder descriptors into scheduled WorkManager jobs. Each sync cancels all work of
 * its kind by tag and re-enqueues from the current data, so it is idempotent and self-healing across
 * subject/appointment edits. Individual jobs use unique-work names so a reschedule just replaces them.
 */
class ReminderScheduler(
  private val context: Context,
  private val now: () -> LocalDateTime = LocalDateTime::now
) {

  private val workManager: WorkManager get() = WorkManager.getInstance(context)

  fun syncPresence(subjects: List<Subject>) {
    workManager.cancelAllWorkByTag(TAG_PRESENCE)
    subjects.forEach { subject ->
      PresenceReminderScheduler.remindersFor(subject, now()).forEach(::enqueuePresence)
    }
  }

  fun syncAppointments(appointments: List<Appointment>, subjectNames: Map<Long, String>) {
    workManager.cancelAllWorkByTag(TAG_APPOINTMENT)
    appointments.forEach { appointment ->
      AppointmentReminderScheduler
        .remindersFor(appointment, now(), appointment.subjectId?.let(subjectNames::get))
        .forEach(::enqueueAppointment)
    }
  }

  fun enqueuePresence(reminder: PresenceReminder) {
    val request = OneTimeWorkRequestBuilder<PresenceReminderWorker>()
      .setInitialDelay(delayMillis(reminder.triggerAt), MILLISECONDS)
      .setInputData(
        workDataOf(
          PresenceReminderWorker.KEY_SUBJECT_ID to reminder.subjectId,
          PresenceReminderWorker.KEY_SUBJECT_NAME to reminder.subjectName,
          PresenceReminderWorker.KEY_DAY_OF_WEEK to reminder.dayOfWeek.value,
          PresenceReminderWorker.KEY_START_AT to reminder.startAt.toString()
        )
      )
      .addTag(TAG_PRESENCE)
      .build()
    workManager.enqueueUniqueWork(reminder.uniqueName, ExistingWorkPolicy.REPLACE, request)
  }

  private fun enqueueAppointment(reminder: AppointmentReminder) {
    val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
      .setInitialDelay(delayMillis(reminder.triggerAt), MILLISECONDS)
      .setInputData(
        workDataOf(
          AppointmentReminderWorker.KEY_APPOINTMENT_ID to reminder.appointmentId,
          AppointmentReminderWorker.KEY_APPOINTMENT_NAME to reminder.appointmentName,
          AppointmentReminderWorker.KEY_SUBJECT_NAME to reminder.subjectName,
          AppointmentReminderWorker.KEY_DAYS_LEFT to reminder.daysLeft
        )
      )
      .addTag(TAG_APPOINTMENT)
      .build()
    workManager.enqueueUniqueWork(reminder.uniqueName, ExistingWorkPolicy.REPLACE, request)
  }

  private fun delayMillis(triggerAt: LocalDateTime): Long = max(0, Duration.between(now(), triggerAt).toMillis())

  companion object {
    const val TAG_PRESENCE = "presence"
    const val TAG_APPOINTMENT = "appointment"
  }
}
