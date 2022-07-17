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

import app.jopiter.restaurant.model.DessertItem.FoodGroup.Doce
import app.jopiter.restaurant.model.DessertItem.FoodGroup.Fruta
import app.jopiter.restaurant.model.DessertItem.Preparation.NaoUltraProcessado
import app.jopiter.restaurant.model.DessertItem.Preparation.UltraProcessado
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class DessertItemTest : FunSpec({
  test("Calculate food group score") {
    listOf(Fruta).forAll { it.score shouldBe 2 }
    listOf(Doce).forAll { it.score shouldBe 0 }
  }

  test("Calculate preparation score") {
    listOf(NaoUltraProcessado).forAll { it.score shouldBe 2 }
    listOf(UltraProcessado).forAll { it.score shouldBe 0 }
  }
})
