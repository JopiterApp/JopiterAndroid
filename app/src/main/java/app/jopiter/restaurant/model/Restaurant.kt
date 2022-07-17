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
)

