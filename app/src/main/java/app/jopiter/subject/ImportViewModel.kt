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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.jopiter.subject.external.JopiterTimetableClient
import app.jopiter.subject.repository.SubjectRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/** Progress of an experimental JupiterWeb timetable import. */
sealed interface ImportState {
  data object Idle : ImportState
  data object Loading : ImportState
  data class Success(val importedCount: Int) : ImportState
  data object Error : ImportState
}

/**
 * Drives the experimental JupiterWeb import: posts the student's credentials to
 * [JopiterTimetableClient], maps the response with [TimetableImporter] and persists the resulting
 * subjects through [SubjectRepository].
 *
 * Credentials are passed straight to the client and never stored on this ViewModel; failures are
 * collapsed to [ImportState.Error] so nothing derived from them (which could echo the input) leaks.
 */
class ImportViewModel(
  private val client: JopiterTimetableClient,
  private val subjectRepository: SubjectRepository,
  private val io: CoroutineDispatcher = Dispatchers.IO,
  private val today: () -> LocalDate = LocalDate::now
) : ViewModel() {

  private val _state = MutableStateFlow<ImportState>(ImportState.Idle)
  val state: StateFlow<ImportState> = _state.asStateFlow()

  fun import(uspNumber: String, password: String) {
    if (uspNumber.isBlank() || password.isBlank()) {
      _state.value = ImportState.Error
      return
    }
    _state.value = ImportState.Loading
    viewModelScope.launch {
      val result = withContext(io) { client.fetchTimetable(uspNumber, password) }
      _state.value = result.fold(
        onSuccess = { timetable ->
          val subjects = TimetableImporter.toSubjects(timetable, today())
          withContext(io) { subjects.forEach(subjectRepository::save) }
          ImportState.Success(subjects.size)
        },
        onFailure = { ImportState.Error }
      )
    }
  }

  fun reset() {
    _state.value = ImportState.Idle
  }
}
