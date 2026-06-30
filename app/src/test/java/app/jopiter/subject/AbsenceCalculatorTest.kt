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

import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters.nextOrSame

class AbsenceCalculatorTest : FunSpec({

  val monday: LocalDate = LocalDate.of(2026, 6, 1).with(nextOrSame(MONDAY))

  fun subject(maxMissed: Int = 8, creation: LocalDate = monday, days: List<DayOfWeek> = listOf(MONDAY)) =
    Subject(
      id = 1,
      name = "Cálculo",
      maxMissedClasses = maxMissed,
      creationDate = creation,
      classTimes = days.map { ClassTime(it, LocalTime.of(8, 0), LocalTime.of(10, 0)) }
    )

  test("a subject without class times never has missed classes") {
    AbsenceCalculator.missedClasses(subject(days = emptyList()), emptySet(), monday.plusWeeks(4)) shouldBe 0
  }

  test("a brand new subject has no past classes to miss") {
    AbsenceCalculator.missedClasses(subject(), emptySet(), monday) shouldBe 0
  }

  test("each past weekly class is missed when attendance was never recorded") {
    // The classes on monday, +1w and +2w are in the past; the +3w class is 'today' and is not counted.
    AbsenceCalculator.missedClasses(subject(), emptySet(), monday.plusWeeks(3)) shouldBe 3
  }

  test("recorded attendance is not counted as a miss") {
    val attended = setOf(monday, monday.plusWeeks(1))
    AbsenceCalculator.missedClasses(subject(), attended, monday.plusWeeks(3)) shouldBe 1
  }

  test("occurrences of every class time are counted") {
    val twiceAWeek = subject(days = listOf(MONDAY, WEDNESDAY))
    AbsenceCalculator.missedClasses(twiceAWeek, emptySet(), monday.plusWeeks(3)) shouldBe 6
  }

  test("remaining misses subtracts from the allowance and may go negative") {
    AbsenceCalculator.remainingMisses(subject(maxMissed = 8), emptySet(), monday.plusWeeks(3)) shouldBe 5
    AbsenceCalculator.remainingMisses(subject(maxMissed = 2), emptySet(), monday.plusWeeks(3)) shouldBe -1
  }
})
