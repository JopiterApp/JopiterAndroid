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
package app.jopiter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.jopiter.R.string.app_name
import app.jopiter.R.string.open_menu_content_description
import app.jopiter.navigation.DrawerContent
import app.jopiter.navigation.JopiterTopBar
import app.jopiter.navigation.Page
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      MainScreen()
    }
  }
}

@Composable
private fun MainScreen() {
  val coroutineScope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(DrawerValue.Closed)

  fun openDrawer() = coroutineScope.launch { drawerState.open() }
  fun closeDrawer() = coroutineScope.launch { drawerState.close() }

  val scaffoldState = rememberScaffoldState(drawerState)
  val (selectedPage, setSelectedPage) = remember { mutableStateOf(Page.Home) }

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = { JopiterTopBar { openDrawer() } },
    drawerContent = { DrawerContent(selectedPage) { setSelectedPage(it); closeDrawer() } }
  ) {
    selectedPage.content()
  }
}
