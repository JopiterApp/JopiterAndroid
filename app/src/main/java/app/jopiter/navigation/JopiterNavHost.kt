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
package app.jopiter.navigation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import app.jopiter.restaurant.RestaurantPage
import app.jopiter.subject.SubjectEditScreen
import app.jopiter.subject.SubjectsPage

/**
 * Hosts every navigable destination. Top-level [Page] routes are reachable from the drawer;
 * pushed detail routes (e.g. subject editing) live here too but are navigated to programmatically.
 */
@Composable
fun JopiterNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
  NavHost(navController, startDestination = Page.Home.route, modifier = modifier) {
    composable(Page.Home.route) { Text("Home", Modifier.testTag("home_content")) }

    composable(Page.Subjects.route) {
      SubjectsPage(
        onAddSubject = { navController.navigate("subjectEdit/0") },
        onEditSubject = { id -> navController.navigate("subjectEdit/$id") }
      )
    }
    composable(
      route = "subjectEdit/{subjectId}",
      arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
    ) { entry ->
      SubjectEditScreen(
        subjectId = entry.arguments?.getLong("subjectId") ?: 0L,
        onDone = { navController.popBackStack() }
      )
    }

    composable(Page.Restaurants.route) { RestaurantPage() }
  }
}
