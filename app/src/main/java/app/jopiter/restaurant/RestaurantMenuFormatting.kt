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
import androidx.compose.ui.graphics.Color
import app.jopiter.R
import app.jopiter.restaurant.model.DessertItem
import app.jopiter.restaurant.model.ProteinItem
import app.jopiter.restaurant.model.RestaurantMenu.Period
import app.jopiter.restaurant.model.VegetarianItem

internal val GoodScore = Color(0xFF2E7D32)
internal val MediumScore = Color(0xFFF9A825)
internal val LowScore = Color(0xFFC62828)

/** Maps an Eco-RU score (2 = best, 1 = ok, 0/0.5 = worst) to its indicator color. */
internal fun scoreColor(value: Double) = when {
  value >= 2 -> GoodScore
  value >= 1 -> MediumScore
  else -> LowScore
}

/** A humanized food-group/preparation label paired with its Eco-RU score. */
data class Score(val label: String, val value: Double)

internal fun ProteinItem.scores() = listOf(
  Score(foodGroup.name.humanize(), foodGroup.score.toDouble()),
  Score(preparation.name.humanize(), preparation.score.toDouble())
)

internal fun VegetarianItem.scores() = listOf(
  Score(foodGroup.name.humanize(), foodGroup.score),
  Score(preparation.name.humanize(), preparation.score.toDouble())
)

internal fun DessertItem.scores() = listOf(
  Score(foodGroup.name.humanize(), foodGroup.score.toDouble()),
  Score(preparation.name.humanize(), preparation.score.toDouble())
)

/** Turns enum names like "AoMolhoLeve" / "GrelhadoRefogado" into "Ao Molho Leve" / "Grelhado Refogado". */
internal fun String.humanize() = replace(Regex("([a-z])([A-Z])"), "$1 $2")

internal val Period.labelRes: Int
  @StringRes get() = when (this) {
    Period.Lunch -> R.string.period_lunch
    Period.Dinner -> R.string.period_dinner
  }
