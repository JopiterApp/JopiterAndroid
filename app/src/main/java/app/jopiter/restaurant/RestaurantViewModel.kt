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
package app.jopiter.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.model.RestaurantMenu
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import app.jopiter.restaurant.repository.RestaurantItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * Holds the Restaurant screen state: the user's preferred restaurant (persisted) and the menus for
 * the current week, grouped by day of week. Replaces the logic that previously lived inline in
 * [RestaurantPage]; this is the canonical ViewModel pattern for the app.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantViewModel(
  private val preferredRestaurantRepository: PreferredRestaurantRepository,
  private val restaurantItemRepository: RestaurantItemRepository
) : ViewModel() {

  val preferredRestaurant: StateFlow<Restaurant> =
    preferredRestaurantRepository.preferredRestaurant
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), Restaurant.DefaultRestaurant)

  @Suppress("InjectDispatcher")
  val menusByDay: StateFlow<Map<DayOfWeek, List<RestaurantMenu>>> =
    preferredRestaurantRepository.preferredRestaurant
      .mapLatest { restaurant -> loadMenus(restaurant) }
      .flowOn(Dispatchers.IO)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyMap())

  fun setPreferredRestaurant(restaurant: Restaurant) =
    preferredRestaurantRepository.setPreferredRestaurant(restaurant)

  private fun loadMenus(restaurant: Restaurant): Map<DayOfWeek, List<RestaurantMenu>> =
    restaurantItemRepository.getRestaurantItems(restaurant.id)
      .filterNot { it.isEmpty() }
      .groupBy { it.date.dayOfWeek.asDayOfWeek() }

  private companion object {
    const val STOP_TIMEOUT_MS = 5000L
  }
}
