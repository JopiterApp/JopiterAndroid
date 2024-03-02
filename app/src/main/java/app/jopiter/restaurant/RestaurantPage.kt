package app.jopiter.restaurant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.jopiter.component.ExposedDropdownMenuBox
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RestaurantPage() {
  val preferredRestaurantRepository = koinInject<PreferredRestaurantRepository>()

  val preferredRestaurant by preferredRestaurantRepository.preferredRestaurant.collectAsState(Restaurant.get(6))
  fun setPreferredRestaurant(restaurant: Restaurant) = preferredRestaurantRepository.setPreferredRestaurant(restaurant)

  var selectedDay by remember { mutableStateOf(DayOfWeek.MONDAY) }

  Column(Modifier.fillMaxWidth()) {

    ExposedDropdownMenuBox(Modifier.fillMaxWidth(), preferredRestaurant.name,"Preferred Restaurant", Restaurant.AllRestaurants, { it.name }, { setPreferredRestaurant(it) } )

    ExposedDropdownMenuBox(Modifier.fillMaxWidth(), selectedDay.displayName, "Day of Week", DayOfWeek.entries, { it.displayName }, { selectedDay = it })
  }
}

enum class DayOfWeek(val displayName: String) {
  MONDAY("Monday"),
  TUESDAY("Tuesday"),
  WEDNESDAY("Wednesday"),
  THURSDAY("Thursday"),
  FRIDAY("Friday"),
  SATURDAY("Saturday"),
  SUNDAY("Sunday")
}
