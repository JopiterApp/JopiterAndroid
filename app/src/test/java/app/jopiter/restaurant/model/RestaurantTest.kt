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
package app.jopiter.restaurant.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class RestaurantTest : FunSpec({

  test("Should map ID to Name correctly") {
    Restaurant.AllRestaurants shouldContainExactly mapOf(
      1L to "Piracicaba",
      2L to "Restaurante área 1",
      3L to "Restaurante área 2",
      4L to "Restaurante CRHEA",
      5L to "Pirassununga",
      6L to "Central",
      7L to "PUSP-C",
      8L to "Física",
      9L to "Químicas",
      11L to "Fac. Saúde Pública",
      12L to "Escola de Enfermagem",
      13L to "EACH",
      14L to "Largo São Francisco",
      17L to "EEL - Área I",
      19L to "Restaurante Central",
      20L to "Bauru",
      23L to "EEL - Área II"
    )
  }

  test("Should contain 17 mapped restaurants (known amount on 2022-07-23)") {
    Restaurant.AllRestaurants shouldHaveSize 17
  }

  context("Find") {
    test("Finds known restaurants") {
      Restaurant.find(13) shouldBe Restaurant(13L, "EACH")
    }
    test("Returns null on unknown restaurants") {
      Restaurant.find(214) shouldBe null
    }
  }

  context("Default restaurant") {
    test("Default restaurant ID should be 6") {
      Restaurant.DefaultRestaurantId shouldBe 6L
    }
    test("Default restaurant name should be Central") {
      Restaurant.DefaultRestaurant.name shouldBe "Central"
    }
  }
})
