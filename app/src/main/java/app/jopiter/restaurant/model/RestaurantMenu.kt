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
@file:Suppress("MagicNumber")

package app.jopiter.restaurant.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantMenu(
  @JsonProperty("restaurantName") val restaurantName: String,
  @JsonProperty("restaurantId") val restaurantId: Int,
  @JsonProperty("date") val date: LocalDate,
  @JsonProperty("period") val period: Period,
  @JsonProperty("calories") val calories: Int,
  @JsonProperty("mainItem") val mainItem: ProteinItem?,
  @JsonProperty("vegetarianItem") val vegetarianItem: VegetarianItem?,
  @JsonProperty("dessertItem") val dessertItem: DessertItem?,
  @JsonProperty("mundaneItems") val mundaneItems: List<String>
) {

  enum class Period { Lunch, Dinner }
}

data class ProteinItem(
  @JsonProperty("item") val item: String,
  @JsonProperty("foodGroup") val foodGroup: FoodGroup,
  @JsonProperty("preparation") val preparation: Preparation
) {
  enum class FoodGroup(val score: Int) {
    Ave(2),
    Ovo(2),
    Peixe(2),
    Bovina(1),
    Suina(1),
    Processada(0)
  }

  enum class Preparation(val score: Int) {
    AoMolhoLeve(2),
    Assado(2),
    Cozido(2),
    GrelhadoRefogado(2),
    AoMolhoGorduroso(1),
    Frito(0)
  }
}

data class VegetarianItem(
  @JsonProperty("item") val item: String,
  @JsonProperty("foodGroup") val foodGroup: FoodGroup,
  @JsonProperty("preparation") val preparation: Preparation
) {
  enum class FoodGroup(val score: Double) {
    Leguminosa(2.0),
    Ovos(2.0),
    PTS(2.0),
    Soja(2.0),
    CerealMilho(1.0),
    Folhosos(1.0),
    Legumes(1.0),
    Queijo(1.0),
    MassaTorta(0.5),
    Tuberculo(0.5)
  }

  enum class Preparation(val score: Int) {
    Assado(2),
    Cozido(2),
    GrelhadoRefogado(2),
    AoMolhoLeve(2),
    AoMolhoGorduroso(1),
    Frito(0)
  }
}

data class DessertItem(
  @JsonProperty("item") val item: String,
  @JsonProperty("foodGroup") val foodGroup: FoodGroup,
  @JsonProperty("preparation") val preparation: Preparation
) {
  enum class FoodGroup(val score: Int) {
    Fruta(2),
    Doce(0)
  }

  enum class Preparation(val score: Int) {
    NaoUltraProcessado(2),
    UltraProcessado(0)
  }
}
