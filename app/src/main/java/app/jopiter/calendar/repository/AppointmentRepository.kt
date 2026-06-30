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
package app.jopiter.calendar.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.jopiter.AppointmentQueries
import app.jopiter.calendar.model.Appointment
import app.jopiter.calendar.model.AppointmentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import app.jopiter.Appointment as AppointmentRow

/** Persists [Appointment]s. Deleting a subject cascades to its appointments. */
class AppointmentRepository(private val queries: AppointmentQueries) {

  @Suppress("InjectDispatcher")
  val appointments: Flow<List<Appointment>> =
    queries.selectAll().asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toAppointment() } }

  fun save(appointment: Appointment): Long = queries.transactionWithResult {
    if (appointment.id == 0L) {
      queries.insert(
        subjectId = appointment.subjectId,
        name = appointment.name,
        description = appointment.description,
        dateTime = appointment.dateTime.toString(),
        type = appointment.type.name
      )
      queries.lastInsertedId().executeAsOne()
    } else {
      queries.update(
        subjectId = appointment.subjectId,
        name = appointment.name,
        description = appointment.description,
        dateTime = appointment.dateTime.toString(),
        type = appointment.type.name,
        id = appointment.id
      )
      appointment.id
    }
  }

  fun delete(id: Long) = queries.deleteById(id)
}

private fun AppointmentRow.toAppointment() = Appointment(
  id = id,
  subjectId = subjectId,
  name = name,
  description = description,
  dateTime = LocalDateTime.parse(dateTime),
  type = AppointmentType.valueOf(type)
)
