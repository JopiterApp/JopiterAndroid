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
import app.jopiter.subject.external.JopiterTimetableClient
import app.jopiter.subject.external.TimetableEntry
import app.jopiter.subject.external.TimetableInformation
import app.jopiter.subject.external.TimetableSubject
import app.jopiter.subject.repository.SubjectRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.time.DayOfWeek
import java.time.DayOfWeek.MONDAY
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ImportViewModelTest : FunSpec({

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  fun repository(): SubjectRepository {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    val database = Database(driver)
    return SubjectRepository(database.subjectQueries, database.classTimeQueries)
  }

  class FakeClient(
    private val result: Result<Map<DayOfWeek, List<TimetableEntry>>>
  ) : JopiterTimetableClient() {
    var calls = 0
      private set

    override fun fetchTimetable(uspNumber: String, password: String): Result<Map<DayOfWeek, List<TimetableEntry>>> {
      calls++
      return result
    }
  }

  val sampleTimetable = mapOf(
    MONDAY to listOf(
      TimetableEntry(
        TimetableSubject("MAC0110", "T1", "08:00:00", "09:40:00"),
        TimetableInformation("Introdução à Computação")
      )
    )
  )

  fun viewModel(client: JopiterTimetableClient, repository: SubjectRepository) =
    ImportViewModel(client, repository, UnconfinedTestDispatcher(), today = { LocalDate.of(2026, 3, 1) })

  test("imports and persists subjects on success") {
    val repository = repository()
    val viewModel = viewModel(FakeClient(Result.success(sampleTimetable)), repository)

    viewModel.import("12345678", "secret")

    viewModel.state.value shouldBe ImportState.Success(1)
    repository.subjects.first().single().name shouldBe "Introdução à Computação"
  }

  test("reports an error and persists nothing on client failure") {
    val repository = repository()
    val viewModel = viewModel(FakeClient(Result.failure(RuntimeException("boom"))), repository)

    viewModel.import("12345678", "secret")

    viewModel.state.value.shouldBeInstanceOf<ImportState.Error>()
    repository.subjects.first().shouldBeEmpty()
  }

  test("rejects blank credentials without calling the client") {
    val client = FakeClient(Result.success(sampleTimetable))
    val viewModel = viewModel(client, repository())

    viewModel.import("  ", "")

    viewModel.state.value.shouldBeInstanceOf<ImportState.Error>()
    client.calls shouldBe 0
  }
})
