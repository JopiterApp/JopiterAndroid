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
package app.jopiter.restaurant

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.jopiter.Database
import app.jopiter.restaurant.external.JopiterRestaurantClient
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import app.jopiter.restaurant.repository.RestaurantItemRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantViewModelTest : FunSpec({

  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)
  val database = Database(driver)
  val preferredRepo = PreferredRestaurantRepository(database.preferredRestaurantQueries)
  // The item repository is never exercised here (menusByDay is not collected), so a dummy host is fine.
  val itemRepo = RestaurantItemRepository(JopiterRestaurantClient("http://localhost:1"))

  beforeTest { Dispatchers.setMain(StandardTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }

  test("starts with the default preferred restaurant") {
    val viewModel = RestaurantViewModel(preferredRepo, itemRepo)
    viewModel.preferredRestaurant.value shouldBe Restaurant.DefaultRestaurant
  }

  test("persists the selected restaurant through the repository") {
    val viewModel = RestaurantViewModel(preferredRepo, itemRepo)
    viewModel.setPreferredRestaurant(Restaurant.get(13))
    database.preferredRestaurantQueries.select().executeAsOne() shouldBe 13L
  }
})
