package app.jopiter.restaurant

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.R.string.preferred_restaurant
import app.jopiter.component.DropdownMenu
import app.jopiter.restaurant.model.Restaurant
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun RestaurantPage(viewModel: RestaurantViewModel = koinViewModel()) {
  val preferredRestaurant by viewModel.preferredRestaurant.collectAsState()
  val restaurantMenus by viewModel.menusByDay.collectAsState()

  var selectedDay by remember { mutableStateOf(DayOfWeek.now()) }

  Column(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = preferredRestaurant.name,
      label = stringResource(preferred_restaurant),
      options = Restaurant.AllRestaurants,
      optionToLabel = { it.name },
      onOptionSelected = { viewModel.setPreferredRestaurant(it) }
    )

    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = stringResource(selectedDay.displayNameRes),
      label = stringResource(R.string.day_of_week),
      options = DayOfWeek.entries,
      optionToLabel = { stringResource(it.displayNameRes) },
      onOptionSelected = { selectedDay = it }
    )

    val menusForSelectedDay = restaurantMenus[selectedDay]
    if(menusForSelectedDay == null) {
      Text("No restasurants are available for this day.", Modifier.fillMaxWidth())
    }

    menusForSelectedDay?.forEach { menu ->
      Text(text = "Periodo: ${menu.period}")
          Text(
              text = "Calorias: ${menu.calories}",
              modifier = Modifier.fillMaxWidth()
          )
          Text(
              text = "Main Item: ${menu.mainItem?.item ?: "N/A"} | Food Group: ${menu.mainItem?.foodGroup} | Food Group Score: ${menu.mainItem?.foodGroup?.score} | Preparation: ${menu.mainItem?.preparation} | Score: ${menu.mainItem?.preparation?.score}",
              modifier = Modifier.fillMaxWidth()
          )

      Text(
        text = "Vegetarian Item: ${menu.vegetarianItem?.item ?: "N/A"} | Food Group: ${menu.vegetarianItem?.foodGroup} | Food Group Score: ${menu.vegetarianItem?.foodGroup?.score} | Preparation: ${menu.vegetarianItem?.preparation} | Score: ${menu.vegetarianItem?.preparation?.score}",
        modifier = Modifier.fillMaxWidth()
      )

      Text(
        text = "Dessert Item: ${menu.dessertItem?.item ?: "N/A"} | Food Group: ${menu.dessertItem?.foodGroup} | Food Group Score: ${menu.dessertItem?.foodGroup?.score} | Preparation: ${menu.dessertItem?.preparation} | Score: ${menu.dessertItem?.preparation?.score}",
        modifier = Modifier.fillMaxWidth()
      )

      Text(
        text = "Outros Itens: ${menu.mundaneItems.joinToString(", ")}", modifier = Modifier.fillMaxWidth()
      )
    }


  }
}

enum class DayOfWeek(@StringRes val displayNameRes: Int) {
  Monday(R.string.monday),
  Tuesday(R.string.tuesday),
  Wednesday(R.string.wednesday),
  Thursday(R.string.thursday),
  Friday(R.string.friday),
  Saturday(R.string.saturday),
  Sunday(R.string.sunday);

  companion object {
    fun now() = LocalDate.now().dayOfWeek.asDayOfWeek()
  }
}

fun java.time.DayOfWeek.asDayOfWeek() = when(this) {
  java.time.DayOfWeek.MONDAY -> DayOfWeek.Monday
  java.time.DayOfWeek.TUESDAY -> DayOfWeek.Tuesday
  java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.Wednesday
  java.time.DayOfWeek.THURSDAY -> DayOfWeek.Thursday
  java.time.DayOfWeek.FRIDAY -> DayOfWeek.Friday
  java.time.DayOfWeek.SATURDAY -> DayOfWeek.Saturday
  java.time.DayOfWeek.SUNDAY -> DayOfWeek.Sunday
}
