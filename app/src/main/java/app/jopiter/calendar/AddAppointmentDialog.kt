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

package app.jopiter.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.calendar.model.Appointment
import app.jopiter.calendar.model.AppointmentType
import app.jopiter.common.TimeFormat
import app.jopiter.component.DropdownMenu
import app.jopiter.subject.model.Subject
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AddAppointmentDialog(
  subjects: List<Subject>,
  initialDate: LocalDate,
  onDismiss: () -> Unit,
  onConfirm: (Appointment) -> Unit
) {
  val form = remember { AppointmentFormState(initialDate) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.add_appointment)) },
    text = { AppointmentForm(form, subjects) },
    confirmButton = {
      TextButton(
        onClick = { if (form.name.isBlank()) form.showNameError = true else onConfirm(form.toAppointment()) },
        modifier = Modifier.testTag("confirm_appointment")
      ) { Text(stringResource(R.string.save)) }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
  )
}

@Composable
private fun AppointmentForm(form: AppointmentFormState, subjects: List<Subject>) {
  val context = LocalContext.current
  Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = spacedBy(8.dp)) {
    OutlinedTextField(
      value = form.name,
      onValueChange = { form.name = it; form.showNameError = false },
      label = { Text(stringResource(R.string.appointment_name_label)) },
      isError = form.showNameError,
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("appointment_name_field")
    )
    if (form.showNameError) {
      Text(
        stringResource(R.string.error_name_required),
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption
      )
    }
    OutlinedTextField(
      value = form.description,
      onValueChange = { form.description = it },
      label = { Text(stringResource(R.string.appointment_description_label)) },
      modifier = Modifier.fillMaxWidth()
    )
    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = stringResource(form.type.labelRes),
      label = stringResource(R.string.appointment_type_label),
      options = AppointmentType.entries,
      optionToLabel = { stringResource(it.labelRes) },
      onOptionSelected = { form.type = it }
    )
    DropdownMenu(
      modifier = Modifier.fillMaxWidth(),
      value = form.subject?.name ?: stringResource(R.string.appointment_subject_none),
      label = stringResource(R.string.appointment_subject_label),
      options = listOf(null) + subjects,
      optionToLabel = { it?.name ?: stringResource(R.string.appointment_subject_none) },
      onOptionSelected = { form.subject = it }
    )
    PickerRow(R.string.appointment_date_label, form.date.format(DateFormat)) {
      pickDate(context, form.date) { form.date = it }
    }
    PickerRow(R.string.appointment_time_label, form.time.format(TimeFormat)) {
      pickTime(context, form.time) { form.time = it }
    }
  }
}

@Composable
private fun PickerRow(labelRes: Int, value: String, onClick: () -> Unit) {
  Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
    Text(stringResource(labelRes))
    OutlinedButton(onClick = onClick) { Text(value) }
  }
}

private class AppointmentFormState(initialDate: LocalDate) {
  var name by mutableStateOf("")
  var description by mutableStateOf("")
  var type by mutableStateOf(AppointmentType.Exam)
  var subject by mutableStateOf<Subject?>(null)
  var date by mutableStateOf(initialDate)
  var time by mutableStateOf(LocalTime.of(8, 0))
  var showNameError by mutableStateOf(false)

  fun toAppointment() = Appointment(
    subjectId = subject?.id,
    name = name.trim(),
    description = description.trim(),
    dateTime = date.atTime(time),
    type = type
  )
}

private fun pickDate(context: Context, initial: LocalDate, onPicked: (LocalDate) -> Unit) {
  DatePickerDialog(
    context,
    { _, year, month, day -> onPicked(LocalDate.of(year, month + 1, day)) },
    initial.year,
    initial.monthValue - 1,
    initial.dayOfMonth
  ).show()
}

private fun pickTime(context: Context, initial: LocalTime, onPicked: (LocalTime) -> Unit) {
  TimePickerDialog(
    context,
    { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) },
    initial.hour,
    initial.minute,
    true
  ).show()
}
