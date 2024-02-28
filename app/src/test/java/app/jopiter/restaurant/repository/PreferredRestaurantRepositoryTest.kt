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
package app.jopiter.restaurant.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.jopiter.Database
import app.jopiter.restaurant.model.Restaurant
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class PreferredRestaurantRepositoryTest : FunSpec({
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  Database.Schema.create(driver)

  val database = Database(driver)

  val target = PreferredRestaurantRepository(database.preferredRestaurantQueries)

  test("Starts with Restaurante Central (id 6) as default") {
    target shouldHavePreferredRestaurant Restaurant.DefaultRestaurant
  }

  test("Persists value to datastore") {
    val newRestaurant = Restaurant(18, "New Restaurant")
    target.setPreferredRestaurant(newRestaurant)
    database.preferredRestaurantQueries.select().executeAsOne() shouldBe 18
  }

  test("Returns updated value") {
    target.setPreferredRestaurant(Restaurant(12, "Escola de Enfermagem"))
    target shouldHavePreferredRestaurant Restaurant(12, "Escola de Enfermagem")

    target.setPreferredRestaurant(Restaurant(14, "Largo São Francisco"))
    target shouldHavePreferredRestaurant Restaurant(14, "Largo São Francisco")
  }
})

private suspend infix fun PreferredRestaurantRepository.shouldHavePreferredRestaurant(restaurant: Restaurant) {
  preferredRestaurant.first() shouldBe restaurant
}

