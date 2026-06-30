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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.previousOrSame

private const val DAYS_IN_WEEK = 7

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
      val parameters = listOf("restaurantId" to "$restaurantId") + weekDates().map { "date" to it.toString() }
      "$apiServer/api/v1/restaurants/items".httpGet(parameters).toJackson<List<RestaurantMenu>>(objectMapper).get()
    }
  }
}

/** The seven days (Monday through Sunday) of the week containing [today]. */
internal fun weekDates(today: LocalDate = LocalDate.now()): List<LocalDate> {
  val monday = today.with(previousOrSame(DayOfWeek.MONDAY))
  return (0 until DAYS_IN_WEEK).map { monday.plusDays(it.toLong()) }
}
