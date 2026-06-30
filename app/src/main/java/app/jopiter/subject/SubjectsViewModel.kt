/*
* Jopiter App
* Copyright (C) 2022 Leonardo Colman Lopes
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jopiter.subject.model.Subject
import app.jopiter.subject.repository.PresenceRepository
import app.jopiter.subject.repository.SubjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/** A subject plus its attendance status, for display on the Subjects screen. */
data class SubjectSummary(
  val subject: Subject,
  val missed: Int,
  val remaining: Int,
  val hasClassToday: Boolean,
  val presentToday: Boolean
)

/** Exposes subjects with their missed-class counts and a per-day presence toggle. */
class SubjectsViewModel(
  private val subjectRepository: SubjectRepository,
  private val presenceRepository: PresenceRepository,
  private val today: () -> LocalDate = LocalDate::now
) : ViewModel() {

  val summaries: StateFlow<List<SubjectSummary>> =
    combine(subjectRepository.subjects, presenceRepository.attendanceBySubject) { subjects, attendance ->
      val day = today()
      subjects.map { subject -> subject.summarize(attendance[subject.id].orEmpty(), day) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

  fun delete(subject: Subject) = subjectRepository.delete(subject.id)

  fun toggleTodayPresence(subjectId: Long) = presenceRepository.togglePresent(subjectId, today())

  private fun Subject.summarize(attended: Set<LocalDate>, day: LocalDate) = SubjectSummary(
    subject = this,
    missed = AbsenceCalculator.missedClasses(this, attended, day),
    remaining = AbsenceCalculator.remainingMisses(this, attended, day),
    hasClassToday = classTimes.any { it.dayOfWeek == day.dayOfWeek },
    presentToday = day in attended
  )

  private companion object {
    const val STOP_TIMEOUT_MS = 5000L
  }
}
