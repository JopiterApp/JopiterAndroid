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

import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDateTime
import java.time.LocalTime

class PresenceReminderSchedulerTest : FunSpec({

  // 2026-06-29 is a Monday.
  val mondayMorning = LocalDateTime.of(2026, 6, 29, 7, 0)

  test("next occurrence is today when the class has not started yet") {
    PresenceReminderScheduler.nextOccurrence(MONDAY, LocalTime.of(8, 0), mondayMorning) shouldBe
      LocalDateTime.of(2026, 6, 29, 8, 0)
  }

  test("next occurrence rolls to next week once the class start has passed") {
    val afterClass = LocalDateTime.of(2026, 6, 29, 9, 0)

    PresenceReminderScheduler.nextOccurrence(MONDAY, LocalTime.of(8, 0), afterClass) shouldBe
      LocalDateTime.of(2026, 7, 6, 8, 0)
  }

  test("next occurrence finds the upcoming weekday later in the week") {
    PresenceReminderScheduler.nextOccurrence(WEDNESDAY, LocalTime.of(10, 0), mondayMorning) shouldBe
      LocalDateTime.of(2026, 7, 1, 10, 0)
  }

  test("builds one reminder per class time with the subject metadata") {
    val subject = Subject(
      id = 42,
      name = "Cálculo",
      classTimes = listOf(
        ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)),
        ClassTime(WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))
      )
    )

    val reminders = PresenceReminderScheduler.remindersFor(subject, mondayMorning)

    reminders.shouldContainExactlyInAnyOrder(
      PresenceReminder(42, "Cálculo", MONDAY, LocalTime.of(8, 0), LocalDateTime.of(2026, 6, 29, 8, 0)),
      PresenceReminder(42, "Cálculo", WEDNESDAY, LocalTime.of(10, 0), LocalDateTime.of(2026, 7, 1, 10, 0))
    )
  }

  test("unique name encodes subject, weekday and start time") {
    PresenceReminder(7, "x", MONDAY, LocalTime.of(8, 0), mondayMorning).uniqueName shouldBe "presence:7:1:08:00"
  }
})
