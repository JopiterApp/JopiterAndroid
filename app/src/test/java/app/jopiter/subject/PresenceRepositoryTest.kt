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
package app.jopiter.subject

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.jopiter.Database
import app.jopiter.subject.repository.PresenceRepository
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class PresenceRepositoryTest : FunSpec({

  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)
  val repository = PresenceRepository(Database(driver).presenceQueries)

  val date = LocalDate.of(2026, 6, 1)

  test("records and reads back a presence") {
    repository.setPresent(subjectId = 1, date = date, present = true)

    repository.isPresent(1, date) shouldBe true
    repository.attendanceBySubject.first()[1] shouldContainExactly setOf(date)
  }

  test("removing a presence clears it") {
    repository.setPresent(1, date, present = true)
    repository.setPresent(1, date, present = false)

    repository.isPresent(1, date) shouldBe false
    repository.attendanceBySubject.first().shouldBeEmpty()
  }

  test("toggling flips the recorded state") {
    repository.togglePresent(1, date)
    repository.isPresent(1, date) shouldBe true

    repository.togglePresent(1, date)
    repository.isPresent(1, date) shouldBe false
  }

  test("recording the same day twice keeps a single presence") {
    repository.setPresent(1, date, present = true)
    repository.setPresent(1, date, present = true)

    repository.attendanceBySubject.first()[1] shouldContainExactly setOf(date)
  }
})
