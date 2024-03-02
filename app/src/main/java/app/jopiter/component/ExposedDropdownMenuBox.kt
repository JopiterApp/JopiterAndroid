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
@Composable
fun <T> ExposedDropdownMenuBox(
  modifier: Modifier = Modifier,
  value: String,
  label: String,
  options: List<T>,
  optionToLabel: (T) -> String,
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
