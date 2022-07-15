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
@file:OptIn(ExperimentalMaterialApi::class)

package app.jopiter.navigation

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.R.string.home_title
import app.jopiter.R.string.jopiter_app
import app.jopiter.R.string.restaurant_title

@Preview
@Composable
fun DrawerContent(selectedPage: Page = Page.Home, setSelectedPage: (Page) -> Unit = {}) {
  Column {
    DrawerTitle()
    Page.values().forEach {
      DrawerRow(it, selectedPage) { setSelectedPage(it) }
    }
  }
}

@Composable
private fun DrawerTitle() {
  Text(
    stringResource(jopiter_app),
    Modifier
      .testTag("drawer_title")
      .padding(16.dp),
    style = MaterialTheme.typography.h5
  )
}


@Composable
private fun DrawerRow(page: Page, selectedPage: Page, onClick: () -> Unit) {
  Box(Modifier.clickable { onClick() }) {
    if (page == selectedPage) SelectedDrawerRow(selectedPage)
    else UnselectedDrawerRow(page)
  }
}

@Composable
private fun SelectedDrawerRow(selectedPage: Page) {
  val title = selectedPage.title(LocalContext.current)

  ListItem(Modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.12f))) {
    Text(title, Modifier.testTag("selected_item"), MaterialTheme.colors.onSurface)
  }
}

@Composable
private fun UnselectedDrawerRow(page: Page) {
  val title = page.title(LocalContext.current)
  ListItem {
    Text(title, Modifier, MaterialTheme.colors.primary)
  }
}


enum class Page(
  @StringRes private val titleRes: Int,
  val content: @Composable () -> Unit
) {
  Home(home_title, { Text("Home") }),
  Restaurants(restaurant_title, { Text("Restaurant") });

  fun title(context: Context) = with(context) { getString(titleRes) }
}
