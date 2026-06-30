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
import app.jopiter.subject.repository.PresenceRepository
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters.nextOrSame

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectsViewModelTest : FunSpec({

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  val monday: LocalDate = LocalDate.of(2026, 6, 1).with(nextOrSame(MONDAY))

  data class Fixture(
    val subjects: SubjectRepository,
    val presence: PresenceRepository,
    val viewModel: SubjectsViewModel
  )

  fun fixture(): Fixture {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    val database = Database(driver)
    val subjects = SubjectRepository(database.subjectQueries, database.classTimeQueries)
    val presence = PresenceRepository(database.presenceQueries)
    return Fixture(subjects, presence, SubjectsViewModel(subjects, presence) { monday.plusWeeks(3) })
  }

  fun mondaySubject(maxMissed: Int = 8) = Subject(
    name = "Cálculo",
    maxMissedClasses = maxMissed,
    creationDate = monday,
    classTimes = listOf(ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)))
  )

  test("summarizes missed classes and today's status") {
    val (subjects, _, viewModel) = fixture()
    subjects.save(mondaySubject())

    val summary = viewModel.summaries.first { it.isNotEmpty() }.single()
    summary.subject.name shouldBe "Cálculo"
    summary.missed shouldBe 3
    summary.remaining shouldBe 5
    summary.hasClassToday shouldBe true
    summary.presentToday shouldBe false
  }

  test("attendance recorded on past classes lowers the missed count") {
    val (subjects, presence, viewModel) = fixture()
    val id = subjects.save(mondaySubject())
    presence.setPresent(id, monday, present = true)
    presence.setPresent(id, monday.plusWeeks(1), present = true)

    val summary = viewModel.summaries.first { it.isNotEmpty() && it.single().missed == 1 }.single()
    summary.missed shouldBe 1
    summary.remaining shouldBe 7
  }

  test("toggling today's presence records attendance for today") {
    val (subjects, _, viewModel) = fixture()
    val id = subjects.save(mondaySubject())

    viewModel.toggleTodayPresence(id)

    viewModel.summaries.first { it.singleOrNull()?.presentToday == true }.single().presentToday shouldBe true
  }
})
