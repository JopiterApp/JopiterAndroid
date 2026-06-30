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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.calendar.model.Appointment
import app.jopiter.calendar.model.AppointmentType
import app.jopiter.common.TimeFormat
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarPage(viewModel: CalendarViewModel = koinViewModel()) {
  val state by viewModel.state.collectAsState()
  var visibleMonth by remember { mutableStateOf(YearMonth.from(state.selectedDate)) }
  var showAddDialog by remember { mutableStateOf(false) }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.testTag("add_appointment_fab")) {
        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_appointment))
      }
    }
  ) { padding ->
    Column(
      Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
      verticalArrangement = spacedBy(12.dp)
    ) {
      TypeFilters(state.activeTypes, viewModel::toggleType)
      MonthHeader(visibleMonth, onPrev = { visibleMonth = visibleMonth.minusMonths(1) }) {
        visibleMonth = visibleMonth.plusMonths(1)
      }
      MonthGrid(visibleMonth, state.appointmentsByDate.keys, state.selectedDate, viewModel::selectDate)
      SelectedDayAppointments(state, viewModel::delete)
    }
  }

  if (showAddDialog) {
    AddAppointmentDialog(
      subjects = state.subjects,
      initialDate = state.selectedDate,
      onDismiss = { showAddDialog = false },
      onConfirm = {
        viewModel.save(it)
        showAddDialog = false
      }
    )
  }
}

@Composable
private fun TypeFilters(active: Set<AppointmentType>, onToggle: (AppointmentType) -> Unit) {
  Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
    AppointmentType.entries.forEach { type ->
      TypeChip(type, type in active, Modifier.weight(1f)) { onToggle(type) }
    }
  }
}

@Composable
private fun TypeChip(type: AppointmentType, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
  val color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
  Surface(
    modifier = modifier.clickable(onClick = onClick),
    color = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.12f) else Color.Transparent,
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, color)
  ) {
    Text(
      stringResource(type.labelRes),
      Modifier.padding(vertical = 6.dp),
      color = color,
      style = MaterialTheme.typography.caption,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun MonthHeader(month: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
  Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
    IconButton(onClick = onPrev) {
      Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.previous_month))
    }
    Text(
      month.atDay(1).format(MonthYearFormat).replaceFirstChar { it.uppercase() },
      style = MaterialTheme.typography.h6
    )
    IconButton(onClick = onNext) {
      Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.next_month))
    }
  }
}

@Composable
private fun MonthGrid(month: YearMonth, marked: Set<LocalDate>, selected: LocalDate, onSelect: (LocalDate) -> Unit) {
  Column(Modifier.fillMaxWidth()) {
    Row(Modifier.fillMaxWidth()) {
      WeekdayHeaders.forEach { label ->
        Text(
          label,
          Modifier.weight(1f),
          color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
          style = MaterialTheme.typography.caption,
          textAlign = TextAlign.Center
        )
      }
    }

    val firstOffset = month.atDay(1).dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    val weeks = (firstOffset + daysInMonth + 6) / 7
    var dayNumber = 1 - firstOffset
    repeat(weeks) {
      Row(Modifier.fillMaxWidth()) {
        repeat(7) {
          if (dayNumber in 1..daysInMonth) {
            val date = month.atDay(dayNumber)
            DayCell(date, date in marked, date == selected, Modifier.weight(1f)) { onSelect(date) }
          } else {
            Spacer(Modifier.weight(1f))
          }
          dayNumber++
        }
      }
    }
  }
}

@Composable
private fun DayCell(date: LocalDate, marked: Boolean, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
  Column(
    modifier
      .aspectRatio(1f)
      .padding(2.dp)
      .clip(CircleShape)
      .background(if (selected) MaterialTheme.colors.primary else Color.Transparent)
      .clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      "${date.dayOfMonth}",
      color = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
      style = MaterialTheme.typography.body2
    )
    if (marked) {
      val dotColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary
      Spacer(Modifier.size(5.dp).clip(CircleShape).background(dotColor))
    }
  }
}

@Composable
private fun SelectedDayAppointments(state: CalendarState, onDelete: (Long) -> Unit) {
  val subjectNames = state.subjects.associate { it.id to it.name }
  if (state.selectedDateAppointments.isEmpty()) {
    Text(
      stringResource(R.string.no_appointments),
      Modifier.fillMaxWidth().testTag("no_appointments"),
      color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    )
  } else {
    Column(verticalArrangement = spacedBy(8.dp)) {
      state.selectedDateAppointments.forEach { appointment ->
        AppointmentCard(appointment, subjectNames[appointment.subjectId]) { onDelete(appointment.id) }
      }
    }
  }
}

@Composable
private fun AppointmentCard(appointment: Appointment, subjectName: String?, onDelete: () -> Unit) {
  Card(Modifier.fillMaxWidth().testTag("appointment_card"), elevation = 2.dp) {
    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
      Column(Modifier.weight(1f), verticalArrangement = spacedBy(2.dp)) {
        Text(appointment.name, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Medium)
        val meta = listOfNotNull(
          stringResource(appointment.type.labelRes),
          appointment.dateTime.toLocalTime().format(TimeFormat),
          subjectName
        ).joinToString(" · ")
        Text(meta, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary)
        if (appointment.description.isNotBlank()) {
          Text(appointment.description, style = MaterialTheme.typography.body2)
        }
      }
      IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
      }
    }
  }
}
