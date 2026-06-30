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
import io.kotest.matchers.collections.shouldContainExactly
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
class SubjectEditViewModelTest : FunSpec({

  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)
  val database = Database(driver)
  val repository = SubjectRepository(database.subjectQueries, database.classTimeQueries)
  val classTime = ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  test("rejects a blank name") {
    val viewModel = SubjectEditViewModel(repository, 0)
    viewModel.addClassTime(classTime)

    viewModel.save() shouldBe false
    viewModel.state.value.nameError shouldBe SubjectNameError.Blank
  }

  test("rejects a subject without class times") {
    val viewModel = SubjectEditViewModel(repository, 0)
    viewModel.onNameChange("Cálculo")

    viewModel.save() shouldBe false
    viewModel.state.value.classTimesError shouldBe true
  }

  test("rejects a duplicate name") {
    repository.save(Subject(name = "Cálculo", classTimes = listOf(classTime)))
    val viewModel = SubjectEditViewModel(repository, 0)
    viewModel.onNameChange("Cálculo")
    viewModel.addClassTime(classTime)

    viewModel.save() shouldBe false
    viewModel.state.value.nameError shouldBe SubjectNameError.Duplicate
  }

  test("saves a valid subject") {
    val viewModel = SubjectEditViewModel(repository, 0)
    viewModel.onNameChange("Cálculo")
    viewModel.onMaxMissedChange("8")
    viewModel.addClassTime(classTime)

    viewModel.save() shouldBe true
    val saved = repository.subjects.first().single()
    saved.name shouldBe "Cálculo"
    saved.maxMissedClasses shouldBe 8
    saved.classTimes shouldContainExactly listOf(classTime)
  }

  test("loads an existing subject for editing") {
    val id = repository.save(Subject(name = "Cálculo", code = "MAT101", classTimes = listOf(classTime)))

    val viewModel = SubjectEditViewModel(repository, id)

    viewModel.state.value.name shouldBe "Cálculo"
    viewModel.state.value.code shouldBe "MAT101"
    viewModel.state.value.classTimes shouldContainExactly listOf(classTime)
  }
})
