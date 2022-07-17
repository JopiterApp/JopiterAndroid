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

import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.CerealMilho
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Folhosos
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Legumes
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Leguminosa
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.MassaTorta
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Ovos
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.PTS
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Queijo
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Soja
import app.jopiter.restaurant.model.VegetarianItem.FoodGroup.Tuberculo
import app.jopiter.restaurant.model.VegetarianItem.Preparation.AoMolhoGorduroso
import app.jopiter.restaurant.model.VegetarianItem.Preparation.AoMolhoLeve
import app.jopiter.restaurant.model.VegetarianItem.Preparation.Assado
import app.jopiter.restaurant.model.VegetarianItem.Preparation.Cozido
import app.jopiter.restaurant.model.VegetarianItem.Preparation.Frito
import app.jopiter.restaurant.model.VegetarianItem.Preparation.GrelhadoRefogado
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class VegetarianItemTest : FunSpec({
  test("Calculate food group scores") {
    listOf(PTS, Soja, Ovos, Leguminosa).forAll { it.score shouldBe 2.0 }
    listOf(CerealMilho, Legumes, Folhosos, Queijo).forAll { it.score shouldBe 1.0 }
    listOf(Tuberculo, MassaTorta).forAll { it.score shouldBe 0.5 }
  }

  test("Calculate preparation scores") {
    listOf(Assado, Cozido, GrelhadoRefogado, AoMolhoLeve).forAll { it.score shouldBe 2 }
    listOf(AoMolhoGorduroso).forAll { it.score shouldBe 1}
    listOf(Frito).forAll { it.score shouldBe 0 }
  }
})
