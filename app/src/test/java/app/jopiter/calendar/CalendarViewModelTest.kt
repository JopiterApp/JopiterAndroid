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

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.jopiter.Database
import app.jopiter.calendar.model.Appointment
import app.jopiter.calendar.model.AppointmentType.Exam
import app.jopiter.calendar.model.AppointmentType.Homework
import app.jopiter.calendar.model.AppointmentType.Other
import app.jopiter.calendar.repository.AppointmentRepository
import app.jopiter.subject.repository.SubjectRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest : FunSpec({

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  val day = LocalDate.of(2026, 6, 10)

  data class Fixture(val appointments: AppointmentRepository, val viewModel: CalendarViewModel)

  fun fixture(): Fixture {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    val database = Database(driver)
    val appointments = AppointmentRepository(database.appointmentQueries)
    val subjects = SubjectRepository(database.subjectQueries, database.classTimeQueries)
    return Fixture(appointments, CalendarViewModel(appointments, subjects) { day })
  }

  test("groups appointments by date") {
    val (appointments, viewModel) = fixture()
    appointments.save(Appointment(name = "A", dateTime = day.atTime(8, 0), type = Exam))
    appointments.save(Appointment(name = "B", dateTime = day.atTime(10, 0), type = Homework))
    appointments.save(Appointment(name = "C", dateTime = day.plusDays(1).atTime(8, 0), type = Other))

    val state = viewModel.state.first { it.appointmentsByDate.size == 2 }
    state.appointmentsByDate.getValue(day).map { it.name } shouldContainExactlyInAnyOrder listOf("A", "B")
    state.appointmentsByDate.getValue(day.plusDays(1)).single().name shouldBe "C"
  }

  test("selected-date appointments follow the selected day") {
    val (appointments, viewModel) = fixture()
    appointments.save(Appointment(name = "Today", dateTime = day.atTime(8, 0), type = Exam))
    appointments.save(Appointment(name = "Tomorrow", dateTime = day.plusDays(1).atTime(8, 0), type = Other))

    viewModel.state.first { it.selectedDateAppointments.isNotEmpty() }
      .selectedDateAppointments.single().name shouldBe "Today"

    viewModel.selectDate(day.plusDays(1))
    viewModel.state.first { it.selectedDate == day.plusDays(1) }
      .selectedDateAppointments.single().name shouldBe "Tomorrow"
  }

  test("toggling a type filters its appointments out") {
    val (appointments, viewModel) = fixture()
    appointments.save(Appointment(name = "Exam", dateTime = day.atTime(8, 0), type = Exam))
    appointments.save(Appointment(name = "Homework", dateTime = day.atTime(9, 0), type = Homework))

    viewModel.toggleType(Exam)

    viewModel.state.first { Exam !in it.activeTypes }
      .selectedDateAppointments.map { it.name } shouldContainExactly listOf("Homework")
  }
})
