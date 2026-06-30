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

import app.jopiter.subject.external.TimetableEntry
import app.jopiter.subject.external.TimetableInformation
import app.jopiter.subject.external.TimetableSubject
import app.jopiter.subject.model.ClassTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDate
import java.time.LocalTime

class TimetableImporterTest : FunSpec({

  fun entry(code: String, name: String, start: String, end: String) =
    TimetableEntry(TimetableSubject(code, "$code-T1", start, end), TimetableInformation(name))

  test("empty timetable yields no subjects") {
    TimetableImporter.toSubjects(emptyMap()).shouldBeEmpty()
  }

  test("maps a single class into a subject with one class time") {
    val timetable = mapOf(MONDAY to listOf(entry("MAC0110", "Introdução à Computação", "08:00:00", "09:40:00")))

    val subject = TimetableImporter.toSubjects(timetable, LocalDate.of(2026, 3, 1)).single()

    subject.name shouldBe "Introdução à Computação"
    subject.code shouldBe "MAC0110"
    subject.creationDate shouldBe LocalDate.of(2026, 3, 1)
    subject.classTimes.shouldContainExactly(
      ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 40))
    )
  }

  test("merges the same subject taught on several days into one subject, sorted by day then time") {
    val timetable = mapOf(
      WEDNESDAY to listOf(entry("MAC0110", "Introdução à Computação", "10:00:00", "11:40:00")),
      MONDAY to listOf(entry("MAC0110", "Introdução à Computação", "08:00:00", "09:40:00"))
    )

    val subject = TimetableImporter.toSubjects(timetable).single()

    subject.code shouldBe "MAC0110"
    subject.classTimes.shouldContainExactly(
      ClassTime(MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 40)),
      ClassTime(WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(11, 40))
    )
  }

  test("keeps distinct subject codes apart") {
    val timetable = mapOf(
      MONDAY to listOf(
        entry("MAC0110", "Introdução à Computação", "08:00:00", "09:40:00"),
        entry("MAT0112", "Vetores e Geometria", "10:00:00", "11:40:00")
      )
    )

    TimetableImporter.toSubjects(timetable).map { it.code } shouldContainExactly listOf("MAC0110", "MAT0112")
  }

  test("falls back to the name when a class has no code") {
    val timetable = mapOf(MONDAY to listOf(entry("", "Optativa Livre", "19:00:00", "20:40:00")))

    val subject = TimetableImporter.toSubjects(timetable).single()

    subject.name shouldBe "Optativa Livre"
    subject.code shouldBe ""
  }
})
