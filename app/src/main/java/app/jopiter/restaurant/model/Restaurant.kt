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
  @JsonProperty("id") val id: Long,
  @JsonProperty("restaurantName") val name: String
) {
  companion object {
    val AllRestaurants = mapOf(
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

    fun get(id: Number) = find(id)!!
    fun find(id: Number): Restaurant? {
      val name = AllRestaurants[id.toLong()]
      return name?.let { Restaurant(id.toLong(), name) }
    }

    const val DefaultRestaurantId = 6
    val DefaultRestaurant = find(DefaultRestaurantId)!!
  }
}
