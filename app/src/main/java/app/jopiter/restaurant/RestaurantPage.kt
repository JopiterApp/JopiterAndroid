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
@file:Suppress("MagicNumber")

package app.jopiter.restaurant

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.component.DropdownMenu
import app.jopiter.restaurant.model.Restaurant
import app.jopiter.restaurant.model.RestaurantMenu
import org.koin.androidx.compose.koinViewModel

@Composable
fun RestaurantPage(viewModel: RestaurantViewModel = koinViewModel()) {
  val preferredRestaurant by viewModel.preferredRestaurant.collectAsState()
  val restaurantMenus by viewModel.menusByDay.collectAsState()

  var selectedDay by remember { mutableStateOf(DayOfWeek.now()) }

  Column(
    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = spacedBy(12.dp)
  ) {
    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = preferredRestaurant.name,
      label = stringResource(R.string.preferred_restaurant),
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

    val menus = restaurantMenus[selectedDay]?.sortedBy { it.period.ordinal }
    if (menus.isNullOrEmpty()) {
      Text(
        stringResource(R.string.no_menu_for_day),
        Modifier.fillMaxWidth().testTag("no_menu"),
        style = MaterialTheme.typography.body1
      )
    } else {
      menus.forEach { MenuCard(it) }
    }
  }
}

@Composable
private fun MenuCard(menu: RestaurantMenu) {
  Card(Modifier.fillMaxWidth().testTag("menu_card"), elevation = 2.dp) {
    Column(Modifier.padding(16.dp), verticalArrangement = spacedBy(12.dp)) {
      Row(Modifier.fillMaxWidth(), spacedBy(8.dp), Alignment.CenterVertically) {
        Text(
          stringResource(menu.period.labelRes),
          Modifier.weight(1f),
          style = MaterialTheme.typography.h6
        )
        Text(
          stringResource(R.string.calories_format, menu.calories),
          color = MaterialTheme.colors.primary,
          style = MaterialTheme.typography.subtitle2
        )
      }
      Divider()
      menu.mainItem?.let { ItemRow(R.string.main_item_label, it.item, it.scores()) }
      menu.vegetarianItem?.let { ItemRow(R.string.vegetarian_item_label, it.item, it.scores()) }
      menu.dessertItem?.let { ItemRow(R.string.dessert_item_label, it.item, it.scores()) }
      if (menu.mundaneItems.isNotEmpty()) OtherItems(menu.mundaneItems)
    }
  }
}

@Composable
private fun ItemRow(@StringRes labelRes: Int, itemName: String, scores: List<Score>) {
  Column(Modifier.fillMaxWidth(), spacedBy(4.dp)) {
    Label(labelRes)
    Text(itemName, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Medium)
    Row(horizontalArrangement = spacedBy(6.dp)) {
      scores.forEach { ScoreChip(it) }
    }
  }
}

@Composable
private fun ScoreChip(score: Score) {
  val color = scoreColor(score.value)
  Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
    Row(
      Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(Modifier.size(8.dp).background(color, CircleShape))
      Spacer(Modifier.width(6.dp))
      Text(score.label, color = color, style = MaterialTheme.typography.caption)
    }
  }
}

@Composable
private fun OtherItems(items: List<String>) {
  Column(verticalArrangement = spacedBy(4.dp)) {
    Label(R.string.other_items_label)
    Text(items.joinToString(", "), style = MaterialTheme.typography.body2)
  }
}

@Composable
private fun Label(@StringRes labelRes: Int) {
  Text(
    stringResource(labelRes).uppercase(),
    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
    style = MaterialTheme.typography.overline
  )
}
