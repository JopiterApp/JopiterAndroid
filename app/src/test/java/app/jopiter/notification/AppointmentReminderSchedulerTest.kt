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
package app.jopiter.notification

import app.jopiter.calendar.model.Appointment
import app.jopiter.calendar.model.AppointmentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AppointmentReminderSchedulerTest : FunSpec({

  val now = LocalDateTime.of(2026, 6, 1, 9, 0)

  fun appointmentAt(dateTime: LocalDateTime, subjectId: Long? = null) =
    Appointment(id = 5, subjectId = subjectId, name = "Prova P1", dateTime = dateTime, type = AppointmentType.Exam)

  test("warns 30/15/7..1 days and 12h before a far-off appointment") {
    val appointment = appointmentAt(now.plusDays(40))

    val reminders = AppointmentReminderScheduler.remindersFor(appointment, now)

    reminders.map { it.daysLeft } shouldContainExactly listOf(30, 15, 7, 6, 5, 4, 3, 2, 1, 0)
    reminders.first { it.daysLeft == 1 }.triggerAt shouldBe appointment.dateTime.minusDays(1)
    reminders.first { it.daysLeft == 0 }.triggerAt shouldBe appointment.dateTime.minusHours(12)
  }

  test("only keeps reminders still in the future") {
    // Appointment in two days: only the 1-day-before and the 12h-before triggers are still ahead.
    val reminders = AppointmentReminderScheduler.remindersFor(appointmentAt(now.plusDays(2)), now)

    reminders.map { it.daysLeft } shouldContainExactly listOf(1, 0)
  }

  test("produces nothing for an appointment already in the past") {
    AppointmentReminderScheduler.remindersFor(appointmentAt(now.minusDays(1)), now).shouldBeEmpty()
  }

  test("carries the subject name through when provided") {
    val appointment = appointmentAt(now.plusDays(40), subjectId = 3)

    val reminders = AppointmentReminderScheduler.remindersFor(appointment, now, "Cálculo")

    reminders.forEach { it.subjectName shouldBe "Cálculo" }
  }

  test("unique name encodes appointment id and days left") {
    AppointmentReminder(5, "Prova", null, 7, now).uniqueName shouldBe "appointment:5:7"
  }
})
