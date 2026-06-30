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
package app.jopiter.calendar

import androidx.annotation.StringRes
import app.jopiter.R
import app.jopiter.calendar.model.AppointmentType
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ptBR = Locale("pt", "BR")

val MonthYearFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", ptBR)

val DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Localized (pt-BR) abbreviated weekday headers, Sunday first. */
val WeekdayHeaders: List<String> = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

val AppointmentType.labelRes: Int
  @StringRes get() = when (this) {
    AppointmentType.Exam -> R.string.appointment_type_exam
    AppointmentType.Assignment -> R.string.appointment_type_assignment
    AppointmentType.Homework -> R.string.appointment_type_homework
    AppointmentType.Other -> R.string.appointment_type_other
  }
