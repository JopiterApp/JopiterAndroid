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
import kotlinx.coroutines.flow.first

class NoteRepositoryTest : FunSpec({

  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)
  val repository = NoteRepository(Database(driver).noteQueries)

  test("adds and reads notes for a subject") {
    repository.add(subjectId = 1, text = "Estudar capítulo 3")

    val note = repository.notesForSubject(1).first().single()
    note.text shouldBe "Estudar capítulo 3"
    note.subjectId shouldBe 1
  }

  test("updates a note") {
    repository.add(1, "antiga")
    val id = repository.notesForSubject(1).first().single().id

    repository.update(id, "nova")

    repository.notesForSubject(1).first().single().text shouldBe "nova"
  }

  test("deletes a note") {
    repository.add(1, "qualquer")
    val id = repository.notesForSubject(1).first().single().id

    repository.delete(id)

    repository.notesForSubject(1).first().shouldBeEmpty()
  }

  test("keeps notes scoped to their subject") {
    repository.add(1, "nota da disciplina 1")
    repository.add(2, "nota da disciplina 2")

    repository.notesForSubject(1).first().single().text shouldBe "nota da disciplina 1"
    repository.notesForSubject(2).first().single().text shouldBe "nota da disciplina 2"
  }
})
