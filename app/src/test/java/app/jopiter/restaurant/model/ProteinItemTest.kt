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

import app.jopiter.restaurant.model.ProteinItem.FoodGroup.Ave
import app.jopiter.restaurant.model.ProteinItem.FoodGroup.Bovina
import app.jopiter.restaurant.model.ProteinItem.FoodGroup.Ovo
import app.jopiter.restaurant.model.ProteinItem.FoodGroup.Peixe
import app.jopiter.restaurant.model.ProteinItem.FoodGroup.Processada
import app.jopiter.restaurant.model.ProteinItem.FoodGroup.Suina
import app.jopiter.restaurant.model.ProteinItem.Preparation.AoMolhoGorduroso
import app.jopiter.restaurant.model.ProteinItem.Preparation.AoMolhoLeve
import app.jopiter.restaurant.model.ProteinItem.Preparation.Assado
import app.jopiter.restaurant.model.ProteinItem.Preparation.Cozido
import app.jopiter.restaurant.model.ProteinItem.Preparation.Frito
import app.jopiter.restaurant.model.ProteinItem.Preparation.GrelhadoRefogado
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ProteinItemTest : FunSpec({
  test("Calculate food group scores") {
    listOf(Ave, Ovo, Peixe).forAll { it.score shouldBe 2 }
    listOf(Bovina, Suina).forAll { it.score shouldBe 1 }
    listOf(Processada).forAll { it.score shouldBe 0 }
  }

  test("Calculate preparation scores") {
    listOf(Assado, Cozido, GrelhadoRefogado, AoMolhoLeve).forAll { it.score shouldBe 2 }
    listOf(AoMolhoGorduroso).forAll { it.score shouldBe 1 }
    listOf(Frito).forAll { it.score shouldBe 0 }
  }
})
