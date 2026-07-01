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

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.jopiter.navigation.DrawerContent
import app.jopiter.navigation.JopiterNavHost
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
  RequestNotificationPermission()

  val coroutineScope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val navController = rememberNavController()

  fun openDrawer() = coroutineScope.launch { drawerState.open() }
  fun closeDrawer() = coroutineScope.launch { drawerState.close() }

  val scaffoldState = rememberScaffoldState(drawerState)
  val currentRoute by navController.currentBackStackEntryAsState()
  val selectedPage = Page.values().firstOrNull { it.route == currentRoute?.destination?.route } ?: Page.Home

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = { JopiterTopBar { openDrawer() } },
    drawerContent = {
      DrawerContent(selectedPage) { page ->
        navController.navigate(page.route) {
          launchSingleTop = true
          restoreState = true
          popUpTo(navController.graph.startDestinationId) { saveState = true }
        }
        closeDrawer()
      }
    }
  ) { padding ->
    JopiterNavHost(navController, Modifier.padding(padding))
  }
}

/** Asks for the notification permission once on launch (Android 13+); a no-op on older versions. */
@Composable
private fun RequestNotificationPermission() {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
  LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
}
