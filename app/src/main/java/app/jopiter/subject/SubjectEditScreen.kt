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

@Composable
fun SubjectEditScreen(
  subjectId: Long,
  onDone: () -> Unit,
  viewModel: SubjectEditViewModel = koinViewModel { parametersOf(subjectId) }
) {
  val state by viewModel.state.collectAsState()
  var showClassTimeDialog by remember { mutableStateOf(false) }

  Column(
    Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    OutlinedTextField(
      value = state.name,
      onValueChange = viewModel::onNameChange,
      label = { Text(stringResource(R.string.subject_name_label)) },
      isError = state.nameError != null,
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("subject_name_field")
    )
    state.nameError?.let {
      Text(stringResource(it.messageRes), color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
    }

    OutlinedTextField(state.code, viewModel::onCodeChange, label = { Text(stringResource(R.string.subject_code_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(state.classroom, viewModel::onClassroomChange, label = { Text(stringResource(R.string.subject_classroom_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(state.lecturer, viewModel::onLecturerChange, label = { Text(stringResource(R.string.subject_lecturer_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(state.lecturerEmail, viewModel::onLecturerEmailChange, label = { Text(stringResource(R.string.subject_lecturer_email_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(
      value = state.maxMissedClasses,
      onValueChange = viewModel::onMaxMissedChange,
      label = { Text(stringResource(R.string.subject_max_missed_label)) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = Modifier.fillMaxWidth()
    )

    Text(stringResource(R.string.class_times_label), style = MaterialTheme.typography.subtitle1)
    state.classTimes.forEachIndexed { index, classTime ->
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(
          stringResource(
            R.string.class_time_summary,
            stringResource(classTime.dayOfWeek.displayNameRes),
            classTime.startAt.format(TimeFormat),
            classTime.endAt.format(TimeFormat)
          )
        )
        IconButton(onClick = { viewModel.removeClassTime(index) }) {
          Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove))
        }
      }
    }
    if (state.classTimesError) {
      Text(stringResource(R.string.error_no_class_time), color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
    }
    OutlinedButton(onClick = { showClassTimeDialog = true }, modifier = Modifier.testTag("add_class_time_button")) {
      Text(stringResource(R.string.add_class_time))
    }

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
private fun ClassTimeDialog(onDismiss: () -> Unit, onConfirm: (ClassTime) -> Unit) {
  val context = LocalContext.current
  var day by remember { mutableStateOf(DayOfWeek.MONDAY) }
  var start by remember { mutableStateOf(LocalTime.of(8, 0)) }
  var end by remember { mutableStateOf(LocalTime.of(10, 0)) }

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
      TextButton(onClick = { onConfirm(ClassTime(day, start, end)) }, modifier = Modifier.testTag("confirm_class_time")) {
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

private fun pickTime(context: Context, initial: LocalTime, onPicked: (LocalTime) -> Unit) {
  TimePickerDialog(context, { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) }, initial.hour, initial.minute, true).show()
}

private val SubjectNameError.messageRes: Int
  @StringRes get() = when (this) {
    SubjectNameError.Blank -> R.string.error_name_blank
    SubjectNameError.Duplicate -> R.string.error_name_duplicate
  }
