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
package app.jopiter.subject.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.jopiter.NoteQueries
import app.jopiter.subject.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import app.jopiter.Note as NoteRow

/** Persists free-text notes per subject (cascades when the subject is deleted). */
class NoteRepository(private val queries: NoteQueries) {

  @Suppress("InjectDispatcher")
  fun notesForSubject(subjectId: Long): Flow<List<Note>> =
    queries.selectBySubject(subjectId).asFlow().mapToList(Dispatchers.IO).map { rows -> rows.map { it.toNote() } }

  fun add(subjectId: Long, text: String) {
    queries.insert(subjectId, text, LocalDateTime.now().toString())
  }

  fun update(id: Long, text: String) {
    queries.update(text, id)
  }

  fun delete(id: Long) = queries.deleteById(id)
}

private fun NoteRow.toNote() = Note(
  id = id,
  subjectId = subjectId,
  text = text,
  creationDate = LocalDateTime.parse(creationDate)
)
