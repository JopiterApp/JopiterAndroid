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
package app.jopiter.component

import androidx.compose.foundation.clickable
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Suppress("LongParameterList")
@Composable
fun <T> DropdownMenu(
  modifier: Modifier = Modifier,
  value: String,
  label: String,
  options: List<T>,
  optionToLabel: @Composable (T) -> String,
  onOptionSelected: (T) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(expanded, { expanded = it }, modifier) {
    TextField(value, {}, label = { Text(label) }, readOnly = true, modifier = modifier.clickable { expanded = true })
    ExposedDropdownMenu(expanded, { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(onClick = { onOptionSelected(option); expanded = false }) {
          Text(optionToLabel(option))
        }
      }
    }
  }
}
