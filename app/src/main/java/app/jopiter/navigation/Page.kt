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

import android.content.Context
import androidx.annotation.StringRes
import app.jopiter.R

/**
 * Top-level destinations shown in the navigation drawer. Each [route] is rendered by [JopiterNavHost];
 * detail screens (e.g. subject editing) are pushed routes that do not appear here.
 */
enum class Page(
  @StringRes private val titleRes: Int,
  val route: String
) {
  Home(R.string.home_title, "home"),
  Subjects(R.string.subjects_title, "subjects"),
  Calendar(R.string.calendar_title, "calendar"),
  Restaurants(R.string.restaurant_title, "restaurants");

  fun title(context: Context) = with(context) { getString(titleRes) }
}
