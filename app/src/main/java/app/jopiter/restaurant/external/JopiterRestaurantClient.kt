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
import io.github.rybalkinsd.kohttp.client.defaultHttpClient
import io.github.rybalkinsd.kohttp.client.fork
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import io.github.rybalkinsd.kohttp.jackson.ext.toJson
import java.time.LocalDate.now

private const val ThreeSecondsInMillis = 3_000L

class JopiterRestaurantClient(
  private val remoteAddress: String,
  private val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())
) {

  private val httpClient = defaultHttpClient.fork {
    readTimeout = ThreeSecondsInMillis
  }

  fun fetchRestaurants() = runCatching {
    httpGet(httpClient) {
      url(remoteAddress)

      path = "/api/v1/restaurants"
    }.use { response ->
      response.toJson().toList().map { objectMapper.convertValue(it, Campus::class.java) }
    }
  }

  fun fetchItems(restaurantId: Int) = runCatching {
    httpGet(httpClient) {
      url(remoteAddress)

      path = "/api/v1/restaurants/items"

      param {
        "restaurantId" to restaurantId
        "date" to listOf("${now()}")
      }
    }.use { response ->
      response.toJson().toList().map { objectMapper.convertValue(it, RestaurantMenu::class.java) }
    }
  }
}
