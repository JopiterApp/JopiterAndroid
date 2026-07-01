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

import app.jopiter.calendar.repository.AppointmentRepository
import app.jopiter.subject.repository.SubjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Keeps scheduled reminders in sync with the data. Observes the subject and appointment flows and
 * re-schedules through [ReminderScheduler] whenever anything changes — so creating, editing or
 * deleting a subject/appointment transparently (re)schedules its notifications.
 */
class ReminderCoordinator(
  private val scheduler: ReminderScheduler,
  private val subjectRepository: SubjectRepository,
  private val appointmentRepository: AppointmentRepository,
  private val scope: CoroutineScope
) {

  fun start() {
    scope.launch { subjectRepository.subjects.collect(scheduler::syncPresence) }
    scope.launch {
      combine(appointmentRepository.appointments, subjectRepository.subjects) { appointments, subjects ->
        appointments to subjects.associate { it.id to it.name }
      }.collect { (appointments, subjectNames) -> scheduler.syncAppointments(appointments, subjectNames) }
    }
  }
}
