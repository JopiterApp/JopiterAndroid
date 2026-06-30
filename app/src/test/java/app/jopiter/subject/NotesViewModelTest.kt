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
import app.jopiter.subject.repository.NoteRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest : FunSpec({

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  data class Fixture(val repository: NoteRepository, val viewModel: NotesViewModel)

  fun fixture(subjectId: Long = 1): Fixture {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    val repository = NoteRepository(Database(driver).noteQueries)
    return Fixture(repository, NotesViewModel(repository, subjectId))
  }

  test("adds a note") {
    val (_, viewModel) = fixture()

    viewModel.add("Lembrar da prova")

    viewModel.notes.first { it.isNotEmpty() }.single().text shouldBe "Lembrar da prova"
  }

  test("ignores blank notes") {
    val (repository, viewModel) = fixture()

    viewModel.add("   ")

    repository.notesForSubject(1).first().shouldBeEmpty()
  }

  test("updates a note") {
    val (_, viewModel) = fixture()
    viewModel.add("primeira")
    val id = viewModel.notes.first { it.isNotEmpty() }.single().id

    viewModel.update(id, "editada")

    viewModel.notes.first { it.singleOrNull()?.text == "editada" }.single().text shouldBe "editada"
  }

  test("deletes a note") {
    val (_, viewModel) = fixture()
    viewModel.add("para apagar")
    val id = viewModel.notes.first { it.isNotEmpty() }.single().id

    viewModel.delete(id)

    viewModel.notes.first { it.isEmpty() }.shouldBeEmpty()
  }
})
