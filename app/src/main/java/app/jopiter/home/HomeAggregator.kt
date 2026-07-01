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
import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import java.time.LocalDate

/** One of today's classes, with whether the student has already marked themselves present. */
data class TodayClass(val subject: Subject, val classTime: ClassTime, val present: Boolean)

/**
 * Pure aggregation behind the Home dashboard: what happens today. No Android or IO dependencies, so
 * it is fully unit-testable.
 */
object HomeAggregator {

  /** The classes happening today, in start-time order, each flagged with today's presence. */
  fun todayClasses(
    subjects: List<Subject>,
    attendance: Map<Long, Set<LocalDate>>,
    today: LocalDate
  ): List<TodayClass> =
    subjects.flatMap { subject ->
      subject.classTimes
        .filter { it.dayOfWeek == today.dayOfWeek }
        .map { classTime -> TodayClass(subject, classTime, today in attendance[subject.id].orEmpty()) }
    }.sortedBy { it.classTime.startAt }

  /** Today's non-empty menus, lunch before dinner. */
  fun todayMenus(menus: List<RestaurantMenu>, today: LocalDate): List<RestaurantMenu> =
    menus.filter { it.date == today && !it.isEmpty() }.sortedBy { it.period }
}
