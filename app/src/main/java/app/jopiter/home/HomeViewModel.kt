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
package app.jopiter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.model.RestaurantMenu
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import app.jopiter.restaurant.repository.RestaurantItemRepository
import app.jopiter.subject.repository.PresenceRepository
import app.jopiter.subject.repository.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/** Everything the Home dashboard shows for today. */
data class HomeState(
  val today: LocalDate,
  val classes: List<TodayClass>,
  val menus: List<RestaurantMenu>,
  val preferredRestaurant: Restaurant
)

/**
 * Aggregates the day's overview — today's classes (with a presence toggle) and today's menu at the
 * preferred restaurant — by reusing the Subject, Presence and Restaurant repositories. The heavy
 * lifting is delegated to the pure [HomeAggregator].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
  subjectRepository: SubjectRepository,
  private val presenceRepository: PresenceRepository,
  preferredRestaurantRepository: PreferredRestaurantRepository,
  private val restaurantItemRepository: RestaurantItemRepository,
  private val today: () -> LocalDate = LocalDate::now
) : ViewModel() {

  private val classes = combine(
    subjectRepository.subjects,
    presenceRepository.attendanceBySubject
  ) { subjects, attendance ->
    HomeAggregator.todayClasses(subjects, attendance, today())
  }

  // Seeded so the (local, instant) class list is not gated behind the restaurant network fetch;
  // the real menus replace the seed once loaded.
  @Suppress("InjectDispatcher")
  private val menus = preferredRestaurantRepository.preferredRestaurant
    .mapLatest { restaurant ->
      restaurant to HomeAggregator.todayMenus(restaurantItemRepository.getRestaurantItems(restaurant.id), today())
    }
    .onStart { emit(Restaurant.DefaultRestaurant to emptyList()) }
    .flowOn(Dispatchers.IO)

  val state: StateFlow<HomeState> =
    combine(classes, menus) { todayClasses, (restaurant, todayMenus) ->
      HomeState(today(), todayClasses, todayMenus, restaurant)
    }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
      HomeState(today(), emptyList(), emptyList(), Restaurant.DefaultRestaurant)
    )

  fun toggleTodayPresence(subjectId: Long) = presenceRepository.togglePresent(subjectId, today())

  private companion object {
    const val STOP_TIMEOUT_MS = 5000L
  }
}
