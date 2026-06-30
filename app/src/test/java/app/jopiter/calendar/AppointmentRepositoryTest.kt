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
import app.jopiter.calendar.model.AppointmentType.Assignment
import app.jopiter.calendar.model.AppointmentType.Exam
import app.jopiter.calendar.repository.AppointmentRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class AppointmentRepositoryTest : FunSpec({

  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)
  val repository = AppointmentRepository(Database(driver).appointmentQueries)

  val exam = Appointment(name = "Prova 1", dateTime = LocalDateTime.of(2026, 6, 10, 8, 0), type = Exam)

  test("saves and reads back an appointment") {
    val id = repository.save(exam)

    id shouldBe 1L
    val saved = repository.appointments.first().single()
    saved.id shouldBe 1L
    saved.name shouldBe "Prova 1"
    saved.type shouldBe Exam
    saved.dateTime shouldBe LocalDateTime.of(2026, 6, 10, 8, 0)
  }

  test("updates an appointment") {
    val id = repository.save(exam)

    repository.save(exam.copy(id = id, name = "Prova final", type = Assignment))

    val saved = repository.appointments.first().single()
    saved.name shouldBe "Prova final"
    saved.type shouldBe Assignment
  }

  test("deletes an appointment") {
    val id = repository.save(exam)

    repository.delete(id)

    repository.appointments.first().shouldBeEmpty()
  }

  test("orders appointments by date") {
    repository.save(exam.copy(name = "Later", dateTime = LocalDateTime.of(2026, 6, 20, 8, 0)))
    repository.save(exam.copy(name = "Earlier", dateTime = LocalDateTime.of(2026, 6, 5, 8, 0)))

    repository.appointments.first().map { it.name } shouldContainExactly listOf("Earlier", "Later")
  }
})
