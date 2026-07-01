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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.common.TimeFormat
import app.jopiter.common.displayNameRes
import app.jopiter.restaurant.labelRes
import app.jopiter.restaurant.model.RestaurantMenu
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomePage(viewModel: HomeViewModel = koinViewModel()) {
  val state by viewModel.state.collectAsState()

  LazyColumn(
    modifier = Modifier.fillMaxSize().testTag("home_content"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item { SectionTitle(stringResource(R.string.home_today_classes)) }
    if (state.classes.isEmpty()) {
      item { Text(stringResource(R.string.home_no_classes), style = MaterialTheme.typography.body2) }
    } else {
      items(state.classes) { todayClass ->
        TodayClassCard(todayClass) { viewModel.toggleTodayPresence(todayClass.subject.id) }
      }
    }

    item { SectionTitle(stringResource(R.string.home_menu_title, state.preferredRestaurant.name)) }
    if (state.menus.isEmpty()) {
      item { Text(stringResource(R.string.home_no_menu), style = MaterialTheme.typography.body2) }
    } else {
      items(state.menus) { menu -> MenuSummaryCard(menu) }
    }
  }
}

@Composable
private fun SectionTitle(text: String) {
  Text(text, style = MaterialTheme.typography.h6, modifier = Modifier.testTag("section_title"))
}

@Composable
private fun TodayClassCard(todayClass: TodayClass, onTogglePresence: () -> Unit) {
  Card(Modifier.fillMaxWidth().testTag("today_class"), elevation = 2.dp) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(todayClass.subject.name, style = MaterialTheme.typography.subtitle1)
      Text(
        stringResource(
          R.string.class_time_summary,
          stringResource(todayClass.classTime.dayOfWeek.displayNameRes),
          todayClass.classTime.startAt.format(TimeFormat),
          todayClass.classTime.endAt.format(TimeFormat)
        ),
        style = MaterialTheme.typography.body2
      )
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(stringResource(R.string.present_today), style = MaterialTheme.typography.body2)
        Switch(
          checked = todayClass.present,
          onCheckedChange = { onTogglePresence() },
          modifier = Modifier.testTag("home_presence_toggle")
        )
      }
    }
  }
}

@Composable
private fun MenuSummaryCard(menu: RestaurantMenu) {
  Card(Modifier.fillMaxWidth().testTag("home_menu"), elevation = 2.dp) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(stringResource(menu.period.labelRes), style = MaterialTheme.typography.subtitle1)
      menu.mainItem?.let { Text(it.item, style = MaterialTheme.typography.body2) }
      menu.vegetarianItem?.let { Text(it.item, style = MaterialTheme.typography.body2) }
      menu.dessertItem?.let { Text(it.item, style = MaterialTheme.typography.body2) }
    }
  }
}
