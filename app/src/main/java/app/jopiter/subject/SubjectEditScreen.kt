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
package app.jopiter.subject

import android.app.TimePickerDialog
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.common.TimeFormat
import app.jopiter.common.displayNameRes
import app.jopiter.component.DropdownMenu
import app.jopiter.subject.model.ClassTime
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.DayOfWeek
import java.time.LocalTime

private const val DEFAULT_START_HOUR = 8
private const val DEFAULT_END_HOUR = 10

@Composable
fun SubjectEditScreen(
  subjectId: Long,
  onDone: () -> Unit,
  viewModel: SubjectEditViewModel = koinViewModel { parametersOf(subjectId) }
) {
  val state by viewModel.state.collectAsState()
  var showClassTimeDialog by remember { mutableStateOf(false) }

  Column(
    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    SubjectFields(state, viewModel)
    ClassTimesSection(state, viewModel) { showClassTimeDialog = true }
    Button(
      onClick = { if (viewModel.save()) onDone() },
      modifier = Modifier.fillMaxWidth().testTag("save_subject_button")
    ) {
      Text(stringResource(R.string.save))
    }
  }

  if (showClassTimeDialog) {
    ClassTimeDialog(
      onDismiss = { showClassTimeDialog = false },
      onConfirm = {
        viewModel.addClassTime(it)
        showClassTimeDialog = false
      }
    )
  }
}

@Composable
private fun SubjectFields(state: SubjectFormState, viewModel: SubjectEditViewModel) {
  LabeledField(
    value = state.name,
    onValueChange = viewModel::onNameChange,
    labelRes = R.string.subject_name_label,
    isError = state.nameError != null,
    testTag = "subject_name_field"
  )
  state.nameError?.let { ErrorText(it.messageRes) }

  LabeledField(state.code, viewModel::onCodeChange, R.string.subject_code_label)
  LabeledField(state.classroom, viewModel::onClassroomChange, R.string.subject_classroom_label)
  LabeledField(state.lecturer, viewModel::onLecturerChange, R.string.subject_lecturer_label)
  LabeledField(state.lecturerEmail, viewModel::onLecturerEmailChange, R.string.subject_lecturer_email_label)
  LabeledField(
    value = state.maxMissedClasses,
    onValueChange = viewModel::onMaxMissedChange,
    labelRes = R.string.subject_max_missed_label,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
  )
}

@Composable
private fun ClassTimesSection(
  state: SubjectFormState,
  viewModel: SubjectEditViewModel,
  onAddClick: () -> Unit
) {
  Text(stringResource(R.string.class_times_label), style = MaterialTheme.typography.subtitle1)
  state.classTimes.forEachIndexed { index, classTime ->
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
      Text(classTime.summary())
      IconButton(onClick = { viewModel.removeClassTime(index) }) {
        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove))
      }
    }
  }
  if (state.classTimesError) ErrorText(R.string.error_no_class_time)
  OutlinedButton(onClick = onAddClick, modifier = Modifier.testTag("add_class_time_button")) {
    Text(stringResource(R.string.add_class_time))
  }
}

@Suppress("LongParameterList")
@Composable
private fun LabeledField(
  value: String,
  onValueChange: (String) -> Unit,
  @StringRes labelRes: Int,
  isError: Boolean = false,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  testTag: String? = null
) {
  val base = Modifier.fillMaxWidth()
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(stringResource(labelRes)) },
    isError = isError,
    singleLine = true,
    keyboardOptions = keyboardOptions,
    modifier = if (testTag != null) base.testTag(testTag) else base
  )
}

@Composable
private fun ErrorText(@StringRes messageRes: Int) {
  Text(
    stringResource(messageRes),
    color = MaterialTheme.colors.error,
    style = MaterialTheme.typography.caption
  )
}

@Composable
private fun ClassTimeDialog(onDismiss: () -> Unit, onConfirm: (ClassTime) -> Unit) {
  val context = LocalContext.current
  var day by remember { mutableStateOf(DayOfWeek.MONDAY) }
  var start by remember { mutableStateOf(LocalTime.of(DEFAULT_START_HOUR, 0)) }
  var end by remember { mutableStateOf(LocalTime.of(DEFAULT_END_HOUR, 0)) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.add_class_time)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DropdownMenu(
          modifier = Modifier.fillMaxWidth(),
          value = stringResource(day.displayNameRes),
          label = stringResource(R.string.day_of_week),
          options = DayOfWeek.values().toList(),
          optionToLabel = { stringResource(it.displayNameRes) },
          onOptionSelected = { day = it }
        )
        TimeRow(R.string.class_time_start, start) { pickTime(context, start) { start = it } }
        TimeRow(R.string.class_time_end, end) { pickTime(context, end) { end = it } }
      }
    },
    confirmButton = {
      TextButton(
        onClick = { onConfirm(ClassTime(day, start, end)) },
        modifier = Modifier.testTag("confirm_class_time")
      ) {
        Text(stringResource(R.string.save))
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
  )
}

@Composable
private fun TimeRow(@StringRes labelRes: Int, time: LocalTime, onClick: () -> Unit) {
  Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
    Text(stringResource(labelRes))
    OutlinedButton(onClick = onClick) { Text(time.format(TimeFormat)) }
  }
}

@Composable
private fun ClassTime.summary(): String = stringResource(
  R.string.class_time_summary,
  stringResource(dayOfWeek.displayNameRes),
  startAt.format(TimeFormat),
  endAt.format(TimeFormat)
)

private fun pickTime(context: Context, initial: LocalTime, onPicked: (LocalTime) -> Unit) {
  TimePickerDialog(
    context,
    { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) },
    initial.hour,
    initial.minute,
    true
  ).show()
}

private val SubjectNameError.messageRes: Int
  @StringRes get() = when (this) {
    SubjectNameError.Blank -> R.string.error_name_blank
    SubjectNameError.Duplicate -> R.string.error_name_duplicate
  }
