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

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.intPreferencesKey
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.model.RestaurantTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class PreferredRestaurantRepositoryTest : FunSpec({
  // TODO Extract to Kotest Listener
  val datastore = PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  val target = PreferredRestaurantRepository(datastore)

  test("Starts with Restaurante Central (id 6) as default") {
    target shouldHavePreferredRestaurant Restaurant.DefaultRestaurant
  }

  test("Persists value to datastore") {
    target.setPreferredRestaurant(18)
    datastore.data.first()[PreferredRestaurantRepository.preferredRestaurantKey] shouldBe 18
  }

  test("Returns updated value") {
    target.setPreferredRestaurant(12)
    target shouldHavePreferredRestaurant Restaurant(12, "Escola de Enfermagem")

    target.setPreferredRestaurant(14)
    target shouldHavePreferredRestaurant Restaurant(14, "Largo São Francisco")
  }
})

private suspend infix fun PreferredRestaurantRepository.shouldHavePreferredRestaurant(restaurant: Restaurant) {
  preferredRestaurant.first() shouldBe restaurant
}

