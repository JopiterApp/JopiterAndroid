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

import app.jopiter.subject.model.Subject
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters.nextOrSame

/**
 * Pure scheduling logic for presence reminders: one reminder per weekly class, due the next time
 * that class starts. Reused both for the initial scheduling and for the weekly reschedule a worker
 * performs after it fires. No Android dependencies, so it is fully unit-testable.
 */
object PresenceReminderScheduler {

  fun remindersFor(subject: Subject, now: LocalDateTime): List<PresenceReminder> =
    subject.classTimes.map { classTime ->
      PresenceReminder(
        subjectId = subject.id,
        subjectName = subject.name,
        dayOfWeek = classTime.dayOfWeek,
        startAt = classTime.startAt,
        triggerAt = nextOccurrence(classTime.dayOfWeek, classTime.startAt, now)
      )
    }

  /** The next date-time a class on [dayOfWeek] at [startAt] starts, strictly after [now]. */
  fun nextOccurrence(dayOfWeek: DayOfWeek, startAt: LocalTime, now: LocalDateTime): LocalDateTime {
    val candidate = now.toLocalDate().with(nextOrSame(dayOfWeek)).atTime(startAt)
    return if (candidate.isAfter(now)) candidate else candidate.plusWeeks(1)
  }
}
