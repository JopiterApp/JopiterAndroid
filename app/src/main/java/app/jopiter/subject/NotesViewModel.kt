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
import app.jopiter.subject.model.Note
import app.jopiter.subject.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Manages the notes of a single subject. */
class NotesViewModel(
  private val repository: NoteRepository,
  private val subjectId: Long
) : ViewModel() {

  val notes: StateFlow<List<Note>> = repository.notesForSubject(subjectId)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

  fun add(text: String) {
    if (text.isNotBlank()) repository.add(subjectId, text.trim())
  }

  fun update(id: Long, text: String) {
    if (text.isNotBlank()) repository.update(id, text.trim())
  }

  fun delete(id: Long) = repository.delete(id)

  private companion object {
    const val STOP_TIMEOUT_MS = 5000L
  }
}
