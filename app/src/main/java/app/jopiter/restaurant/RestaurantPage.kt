package app.jopiter.restaurant

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.jopiter.R
import app.jopiter.R.string.preferred_restaurant
import app.jopiter.component.DropdownMenu
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import org.koin.compose.koinInject

@Composable
fun RestaurantPage() {
  val preferredRestaurantRepository = koinInject<PreferredRestaurantRepository>()

  val preferredRestaurant by preferredRestaurantRepository.preferredRestaurant.collectAsState(Restaurant.get(6))
  fun setPreferredRestaurant(restaurant: Restaurant) = preferredRestaurantRepository.setPreferredRestaurant(restaurant)

  var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }

  Column(Modifier.fillMaxWidth()) {

    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = preferredRestaurant.name,
      label = stringResource(preferred_restaurant),
      options = Restaurant.AllRestaurants,
      optionToLabel = { it.name },
      onOptionSelected = { setPreferredRestaurant(it) }
    )

    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = stringResource(selectedDay.displayNameRes),
      label = stringResource(R.string.day_of_week),
      options = DayOfWeek.entries,
      optionToLabel = { stringResource(it.displayNameRes) },
      onOptionSelected = { selectedDay = it }
    )
  }
}

enum class DayOfWeek(@StringRes val displayNameRes: Int) {
  MONDAY(R.string.monday),
  TUESDAY(R.string.tuesday),
  WEDNESDAY(R.string.wednesday),
  THURSDAY(R.string.thursday),
  FRIDAY(R.string.friday),
  SATURDAY(R.string.saturday),
  SUNDAY(R.string.sunday)
}
