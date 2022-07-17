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
  enum class FoodGroup { Ave, Bovina, Ovo, Peixe, Suina, Processada }
  enum class Preparation { AoMolhoGorduroso, Assado, Cozido, Frito, GrelhadoRefogado, AoMolhoLeve }
}

data class VegetarianItem(
  @JsonProperty("item") val item: String,
  @JsonProperty("foodGroup") val foodGroup: FoodGroup,
  @JsonProperty("preparation") val preparation: Preparation
) {
  enum class FoodGroup { CerealMilho, Tuberculo, Legumes, Folhosos, Leguminosa, PTS, Soja, MassaTorta, Ovos, Queijo }
  enum class Preparation { AoMolhoGorduroso, Assado, Cozido, Frito, GrelhadoRefogado, AoMolhoLeve }
}

data class DessertItem(
  @JsonProperty("item") val item: String,
  @JsonProperty("foodGroup") val foodGroup: FoodGroup,
  @JsonProperty("preparation") val preparation: Preparation
) {
  enum class FoodGroup { Doce, Fruta }
  enum class Preparation { UltraProcessado, NaoUltraProcessado }
}
