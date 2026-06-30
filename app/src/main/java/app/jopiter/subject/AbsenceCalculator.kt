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

import app.jopiter.subject.model.Subject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.nextOrSame

/**
 * Counts missed classes the way the legacy app did: a scheduled class is "missed" unless the student
 * recorded attendance ([attendedDates]) for that day. Only classes strictly before [today] are
 * counted, so today's still-confirmable class never shows as a miss.
 */
object AbsenceCalculator {

  fun missedClasses(subject: Subject, attendedDates: Set<LocalDate>, today: LocalDate): Int =
    scheduledOccurrences(subject, today).count { it !in attendedDates }

  /** Allowed misses still available. Negative once the subject's limit has been exceeded. */
  fun remainingMisses(subject: Subject, attendedDates: Set<LocalDate>, today: LocalDate): Int =
    subject.maxMissedClasses - missedClasses(subject, attendedDates, today)

  private fun scheduledOccurrences(subject: Subject, today: LocalDate): List<LocalDate> =
    subject.classTimes.flatMap { occurrences(it.dayOfWeek, subject.creationDate, today) }

  private fun occurrences(day: DayOfWeek, from: LocalDate, until: LocalDate): List<LocalDate> {
    val firstOccurrence = from.with(nextOrSame(day))
    return generateSequence(firstOccurrence) { it.plusWeeks(1) }
      .takeWhile { it.isBefore(until) }
      .toList()
  }
}
