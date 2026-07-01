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
package app.jopiter.home

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.jopiter.Database
import app.jopiter.restaurant.external.JopiterRestaurantClient
import app.jopiter.restaurant.model.RestaurantMenu
import app.jopiter.restaurant.model.RestaurantMenu.Period
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import app.jopiter.restaurant.repository.RestaurantItemRepository
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
import java.time.DayOfWeek.TUESDAY
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : FunSpec({

  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  // 2026-06-29 is a Monday.
  val monday = LocalDate.of(2026, 6, 29)

  class FakeItemRepository(private val menus: List<RestaurantMenu>) :
    RestaurantItemRepository(JopiterRestaurantClient("http://localhost")) {
    override fun getRestaurantItems(restaurantId: Int) = menus
  }

  fun menu(period: Period) = RestaurantMenu("RU", 6, monday, period, 0, null, null, null, listOf("Arroz"))

  data class Fixture(val subjects: SubjectRepository, val viewModel: HomeViewModel)

  fun fixture(menus: List<RestaurantMenu> = emptyList()): Fixture {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    val database = Database(driver)
    val subjects = SubjectRepository(database.subjectQueries, database.classTimeQueries)
    val presence = PresenceRepository(database.presenceQueries)
    val preferred = PreferredRestaurantRepository(database.preferredRestaurantQueries)
    val viewModel = HomeViewModel(subjects, presence, preferred, FakeItemRepository(menus), today = { monday })
    return Fixture(subjects, viewModel)
  }

  test("exposes today's classes and today's menus") {
    val (subjects, viewModel) = fixture(listOf(menu(Period.Dinner), menu(Period.Lunch)))
    subjects.save(
      Subject(
        name = "Cálculo",
        classTimes = listOf(
          ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)),
          ClassTime(TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))
        )
      )
    )

    val state = viewModel.state.first { it.classes.isNotEmpty() }

    state.classes.single().subject.name shouldBe "Cálculo"
    state.classes.single().classTime.dayOfWeek shouldBe MONDAY
    state.menus.map { it.period } shouldBe listOf(Period.Lunch, Period.Dinner)
  }

  test("toggling presence marks the student present for today") {
    val (subjects, viewModel) = fixture()
    val id = subjects.save(
      Subject(name = "Cálculo", classTimes = listOf(ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))))
    )

    viewModel.toggleTodayPresence(id)

    viewModel.state.first { it.classes.singleOrNull()?.present == true }.classes.single().present shouldBe true
  }
})
