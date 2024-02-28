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
package app.jopiter.restaurant.external

import app.jopiter.restaurant.model.Campus
import app.jopiter.restaurant.model.RestaurantMenu
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fuel.httpGet
import fuel.jackson.toJackson
import kotlinx.coroutines.runBlocking
import java.time.LocalDate.now

class JopiterRestaurantClient(
  private val apiServer: String,
  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
) {

  fun fetchRestaurants() = runCatching {
    runBlocking {
     "$apiServer/api/v1/restaurants".httpGet().toJackson<List<Campus>>(objectMapper).get()
    }
  }

  fun fetchItems(restaurantId: Int) = runCatching {
    runBlocking {
      "$apiServer/api/v1/restaurants/items".httpGet(
        listOf("restaurantId" to "$restaurantId", "date" to "${now()}")
      ).toJackson<List<RestaurantMenu>>(objectMapper).get()
    }
  }
}
