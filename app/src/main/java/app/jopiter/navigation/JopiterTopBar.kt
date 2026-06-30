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
package app.jopiter.navigation

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.jopiter.R.string.app_name
import app.jopiter.R.string.open_menu_content_description

@Composable
fun JopiterTopBar(onNavigationClick: () -> Unit) {
  val title = stringResource(app_name)
  val iconDescription = stringResource(open_menu_content_description)

  TopAppBar(
    title = { Text(title, Modifier.testTag("top_app_bar_title")) },
    navigationIcon = {
      Icon(
        Icons.Default.Menu,
        iconDescription,
        Modifier
          .clickable { onNavigationClick() }
          .testTag("top_app_bar_icon")
      )
    },
    modifier = Modifier.testTag("top_app_bar")
  )
}
