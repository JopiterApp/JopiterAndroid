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
package app.jopiter.home

import app.jopiter.restaurant.model.RestaurantMenu
import app.jopiter.restaurant.model.RestaurantMenu.Period
import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.TUESDAY
import java.time.LocalDate
import java.time.LocalTime

class HomeAggregatorTest : FunSpec({

  // 2026-06-29 is a Monday.
  val monday = LocalDate.of(2026, 6, 29)

  fun classTime(day: java.time.DayOfWeek, hour: Int) = ClassTime(day, LocalTime.of(hour, 0), LocalTime.of(hour + 2, 0))

  fun menu(period: Period, date: LocalDate = monday, empty: Boolean = false) = RestaurantMenu(
    restaurantName = "RU",
    restaurantId = 6,
    date = date,
    period = period,
    calories = 0,
    mainItem = null,
    vegetarianItem = null,
    dessertItem = null,
    mundaneItems = if (empty) emptyList() else listOf("Arroz")
  )

  context("today's classes") {
    test("keeps only classes on today's weekday, ordered by start time, flagged with presence") {
      val subjects = listOf(
        Subject(id = 1, name = "Física", classTimes = listOf(classTime(MONDAY, 10))),
        Subject(id = 2, name = "Cálculo", classTimes = listOf(classTime(MONDAY, 8), classTime(TUESDAY, 8)))
      )
      val attendance = mapOf(2L to setOf(monday))

      val today = HomeAggregator.todayClasses(subjects, attendance, monday)

      today.map { it.subject.name } shouldBe listOf("Cálculo", "Física")
      today.map { it.classTime.dayOfWeek } shouldBe listOf(MONDAY, MONDAY)
      today.first().present shouldBe true
      today.last().present shouldBe false
    }

    test("is empty when no class falls on today") {
      val subjects = listOf(Subject(id = 1, name = "Cálculo", classTimes = listOf(classTime(TUESDAY, 8))))

      HomeAggregator.todayClasses(subjects, emptyMap(), monday).shouldBeEmpty()
    }
  }

  context("today's menus") {
    test("keeps today's non-empty menus, lunch before dinner") {
      val menus = listOf(
        menu(Period.Dinner),
        menu(Period.Lunch),
        menu(Period.Lunch, date = monday.plusDays(1)),
        menu(Period.Lunch, empty = true)
      )

      HomeAggregator.todayMenus(menus, monday).map { it.period } shouldBe listOf(Period.Lunch, Period.Dinner)
    }
  }
})
