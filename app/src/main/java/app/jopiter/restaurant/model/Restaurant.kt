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

import com.fasterxml.jackson.annotation.JsonProperty

data class Campus(
  @JsonProperty("campusName") val campusName: String,
  @JsonProperty("restaurants") val restaurants: List<Restaurant>
)

data class Restaurant(
  @JsonProperty("id") val id: Int,
  @JsonProperty("restaurantName") val name: String
) {
  companion object {
    val AllRestaurants = mapOf(
      1 to "Piracicaba",
      2 to "Restaurante área 1",
      3 to "Restaurante área 2",
      4 to "Restaurante CRHEA",
      5 to "Pirassununga",
      6 to "Central - Campus Butantã",
      7 to "PUSP-C - Campus Butantã",
      8 to "Física - Campus Butantã",
      9 to "Químicas - Campus Butantã",
      11 to "Fac. Saúde Pública",
      12 to "Escola de Enfermagem",
      13 to "EACH",
      14 to "Fac. Direito",
      17 to "EEL - Área I",
      19 to "Restaurante Central -Campus RP",
      20 to "Bauru",
      23 to "EEL - Área II"
    ).map { Restaurant(it.key, it.value) }

    fun get(id: Int) = find(id)!!
    fun find(id: Int) = AllRestaurants.find { it.id == id }

    const val DefaultRestaurantId = 6
    val DefaultRestaurant = find(DefaultRestaurantId)!!
  }
}
