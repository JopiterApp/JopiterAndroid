package app.jopiter.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.repository.PreferredRestaurantRepository
import app.jopiter.restaurant.repository.RestaurantItemRepository
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RestaurantPage() {
  val preferredRestaurantRepository = koinInject<PreferredRestaurantRepository>()
  val restaurantItemRepository = koinInject<RestaurantItemRepository>()

  val preferredRestaurant by preferredRestaurantRepository.preferredRestaurant.collectAsState(Restaurant.get(6))
  fun setPreferredRestaurant(restaurant: Restaurant) = preferredRestaurantRepository.setPreferredRestaurant(restaurant)

  var expanded by remember { mutableStateOf(false) }

  Column(Modifier.fillMaxWidth()) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, Modifier.fillMaxWidth()) {
      TextField(
        value = preferredRestaurant.name,
        onValueChange = {},
        label = { Text(text = "Preferred Restaurant") },
        readOnly = true,
        modifier = Modifier
          .clickable { expanded = true }
          .fillMaxWidth())

      ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
      ) {
        Restaurant.AllRestaurants.forEach { restaurant ->
          DropdownMenuItem(onClick = {
            setPreferredRestaurant(restaurant)
            expanded = false
          }) {
            Text(restaurant.name)
          }
        }
      }
    }

    val restaurantItems = restaurantItemRepository.getRestaurantItems(preferredRestaurant.id)

    Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        ItemImageAndText(item = MenuItems.Main)
        ItemImageAndText(item = MenuItems.Vegetarian)
        ItemImageAndText(item = MenuItems.Dessert)
    }
  }
}

@Composable
fun RowScope.ItemImageAndText(item: MenuItem) {
  Column(Modifier.weight(1f), Arrangement.Center, Alignment.CenterHorizontally) {
    Image(painter = painterResource(id = item.imageResId), contentDescription = item.name, modifier = Modifier
      .fillMaxWidth()
      .height(128.dp))
    Text(item.name)
  }
}

data class MenuItem(val name: String, val imageResId: Int)
object MenuItems {
  val Main = MenuItem("Main Dish", R.drawable.ic_ecoru_meat)
  val Vegetarian = MenuItem("Vegetarian Dish", R.drawable.ic_ecoru_vegetables)
  val Dessert = MenuItem("Dessert", R.drawable.ic_ecoru_apple)
}
