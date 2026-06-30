/*
* Jopiter App
* Copyright (C) 2022 Leonardo Colman Lopes
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
import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import app.jopiter.subject.repository.SubjectRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.time.DayOfWeek.MONDAY
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectsViewModelTest : FunSpec({

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  test("exposes subjects from the repository") {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    val database = Database(driver)
    val repository = SubjectRepository(database.subjectQueries, database.classTimeQueries)
    repository.save(Subject(name = "Cálculo", classTimes = listOf(ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)))))

    val viewModel = SubjectsViewModel(repository)

    viewModel.subjects.first { it.isNotEmpty() }.single().name shouldBe "Cálculo"
  }
})
