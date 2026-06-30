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
package app.jopiter.restaurant

import androidx.annotation.StringRes
import app.jopiter.R
import java.time.LocalDate

/** Day of week used by the restaurant menu screen, carrying its localized display name. */
enum class DayOfWeek(@StringRes val displayNameRes: Int) {
  Monday(R.string.monday),
  Tuesday(R.string.tuesday),
  Wednesday(R.string.wednesday),
  Thursday(R.string.thursday),
  Friday(R.string.friday),
  Saturday(R.string.saturday),
  Sunday(R.string.sunday);

  companion object {
    fun now() = LocalDate.now().dayOfWeek.asDayOfWeek()
  }
}

fun java.time.DayOfWeek.asDayOfWeek() = when (this) {
  java.time.DayOfWeek.MONDAY -> DayOfWeek.Monday
  java.time.DayOfWeek.TUESDAY -> DayOfWeek.Tuesday
  java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.Wednesday
  java.time.DayOfWeek.THURSDAY -> DayOfWeek.Thursday
  java.time.DayOfWeek.FRIDAY -> DayOfWeek.Friday
  java.time.DayOfWeek.SATURDAY -> DayOfWeek.Saturday
  java.time.DayOfWeek.SUNDAY -> DayOfWeek.Sunday
}
