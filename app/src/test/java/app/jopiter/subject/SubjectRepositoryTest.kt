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
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalTime

class SubjectRepositoryTest : FunSpec({

  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)
  val database = Database(driver)
  val repository = SubjectRepository(database.subjectQueries, database.classTimeQueries)

  val classTimes = listOf(
    ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)),
    ClassTime(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))
  )

  test("saves a new subject with its class times") {
    val id = repository.save(Subject(name = "Cálculo", code = "MAT101", maxMissedClasses = 8, classTimes = classTimes))

    id shouldBe 1L
    val saved = repository.subjects.first().single()
    saved.id shouldBe 1L
    saved.name shouldBe "Cálculo"
    saved.code shouldBe "MAT101"
    saved.maxMissedClasses shouldBe 8
    saved.classTimes shouldContainExactly classTimes
  }

  test("updates an existing subject and replaces its class times") {
    val id = repository.save(Subject(name = "Física", classTimes = classTimes))
    val newTimes = listOf(ClassTime(FRIDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)))

    repository.save(Subject(id = id, name = "Física II", classTimes = newTimes))

    val saved = repository.subjects.first().single()
    saved.name shouldBe "Física II"
    saved.classTimes shouldContainExactly newTimes
  }

  test("deletes a subject and its class times") {
    val id = repository.save(Subject(name = "Química", classTimes = classTimes))

    repository.delete(id)

    repository.subjects.first().shouldBeEmpty()
    database.classTimeQueries.selectBySubject(id).executeAsList().shouldBeEmpty()
  }

  test("orders subjects by name") {
    repository.save(Subject(name = "Zoologia", classTimes = classTimes))
    repository.save(Subject(name = "Anatomia", classTimes = classTimes))

    repository.subjects.first().map { it.name } shouldContainExactly listOf("Anatomia", "Zoologia")
  }

  context("isNameTaken") {
    test("is true when another subject uses the name") {
      repository.save(Subject(name = "Cálculo", classTimes = classTimes))
      repository.isNameTaken("Cálculo", excludingId = 0) shouldBe true
    }
    test("is false for the same subject being edited") {
      val id = repository.save(Subject(name = "Cálculo", classTimes = classTimes))
      repository.isNameTaken("Cálculo", excludingId = id) shouldBe false
    }
    test("is false for an unused name") {
      repository.isNameTaken("Inexistente", excludingId = 0) shouldBe false
    }
  }
})
