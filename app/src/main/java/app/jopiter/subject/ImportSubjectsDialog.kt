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
package app.jopiter.subject

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.jopiter.R
import org.koin.androidx.compose.koinViewModel

private const val FEEDBACK_URL = "https://github.com/JopiterApp/JopiterAndroid/issues"

/**
 * Credential dialog for the experimental JupiterWeb import. Collects the student's USP number and
 * password, hands them to [ImportViewModel], and closes itself once the import succeeds. Makes the
 * experimental nature explicit and links to GitHub issues for feedback.
 */
@Composable
fun ImportSubjectsDialog(
  onDismiss: () -> Unit,
  viewModel: ImportViewModel = koinViewModel()
) {
  val state by viewModel.state.collectAsState()
  var uspNumber by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  val loading = state is ImportState.Loading

  LaunchedEffect(state) {
    if (state is ImportState.Success) onDismiss()
  }

  AlertDialog(
    onDismissRequest = { if (!loading) onDismiss() },
    title = { Text(stringResource(R.string.import_subjects_title)) },
    text = { ImportForm(uspNumber, password, state, { uspNumber = it }, { password = it }) },
    confirmButton = {
      TextButton(
        onClick = { viewModel.import(uspNumber, password) },
        enabled = !loading,
        modifier = Modifier.testTag("import_confirm")
      ) { Text(stringResource(R.string.import_subjects_action)) }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, enabled = !loading) { Text(stringResource(R.string.cancel)) }
    }
  )
}

@Composable
private fun ImportForm(
  uspNumber: String,
  password: String,
  state: ImportState,
  onUspNumberChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit
) {
  val uriHandler = LocalUriHandler.current
  Column(verticalArrangement = spacedBy(8.dp)) {
    Text(
      stringResource(R.string.import_subjects_experimental),
      style = MaterialTheme.typography.body2
    )
    TextButton(
      onClick = { uriHandler.openUri(FEEDBACK_URL) },
      modifier = Modifier.testTag("import_feedback")
    ) { Text(stringResource(R.string.import_subjects_feedback)) }

    OutlinedTextField(
      value = uspNumber,
      onValueChange = onUspNumberChange,
      label = { Text(stringResource(R.string.import_subjects_usp_number)) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = Modifier.fillMaxWidth().testTag("import_usp_number")
    )
    OutlinedTextField(
      value = password,
      onValueChange = onPasswordChange,
      label = { Text(stringResource(R.string.import_subjects_password)) },
      singleLine = true,
      visualTransformation = PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      modifier = Modifier.fillMaxWidth().testTag("import_password")
    )

    when (state) {
      is ImportState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
      is ImportState.Error -> Text(
        stringResource(R.string.import_subjects_error),
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption
      )
      else -> Unit
    }
  }
}
