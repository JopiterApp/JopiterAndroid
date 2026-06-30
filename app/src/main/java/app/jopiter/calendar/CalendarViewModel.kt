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
package app.jopiter.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jopiter.calendar.model.Appointment
import app.jopiter.calendar.model.AppointmentType
import app.jopiter.calendar.repository.AppointmentRepository
import app.jopiter.subject.model.Subject
import app.jopiter.subject.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/** What the calendar screen renders: appointments grouped by day, filtered by the active types. */
data class CalendarState(
  val appointmentsByDate: Map<LocalDate, List<Appointment>> = emptyMap(),
  val activeTypes: Set<AppointmentType> = AppointmentType.entries.toSet(),
  val selectedDate: LocalDate = LocalDate.now(),
  val selectedDateAppointments: List<Appointment> = emptyList(),
  val subjects: List<Subject> = emptyList()
)

class CalendarViewModel(
  private val appointmentRepository: AppointmentRepository,
  private val subjectRepository: SubjectRepository,
  today: () -> LocalDate = LocalDate::now
) : ViewModel() {

  private val selectedDate = MutableStateFlow(today())
  private val activeTypes = MutableStateFlow(AppointmentType.entries.toSet())

  val state: StateFlow<CalendarState> = combine(
    appointmentRepository.appointments,
    subjectRepository.subjects,
    selectedDate,
    activeTypes
  ) { appointments, subjects, date, types ->
    val filtered = appointments.filter { it.type in types }
    CalendarState(
      appointmentsByDate = filtered.groupBy { it.dateTime.toLocalDate() },
      activeTypes = types,
      selectedDate = date,
      selectedDateAppointments = filtered.filter { it.dateTime.toLocalDate() == date }.sortedBy { it.dateTime },
      subjects = subjects
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
    CalendarState(selectedDate = selectedDate.value)
  )

  fun selectDate(date: LocalDate) {
    selectedDate.value = date
  }

  fun toggleType(type: AppointmentType) = activeTypes.update { types ->
    if (type in types) types - type else types + type
  }

  fun save(appointment: Appointment) {
    appointmentRepository.save(appointment)
  }

  fun delete(id: Long) = appointmentRepository.delete(id)

  private companion object {
    const val STOP_TIMEOUT_MS = 5000L
  }
}
