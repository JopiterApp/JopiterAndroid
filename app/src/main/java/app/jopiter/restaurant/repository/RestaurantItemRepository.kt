/*
* Jopiter App
* Copyright (C) 2026 Leonardo Colman Lopes
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
package app.jopiter.restaurant.repository

import app.jopiter.restaurant.external.JopiterRestaurantClient
import app.jopiter.restaurant.model.RestaurantMenu

class RestaurantItemRepository(private val jopiterClient: JopiterRestaurantClient) {

  fun getRestaurantItems(restaurantId: Int): List<RestaurantMenu> {
    return jopiterClient.fetchItems(restaurantId).getOrNull().orEmpty()
  }
}
