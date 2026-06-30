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
@file:Suppress("MagicNumber")

package app.jopiter.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.subject.model.Note
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter

private val NoteDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@Composable
fun SubjectNotesScreen(
  subjectId: Long,
  viewModel: NotesViewModel = koinViewModel { parametersOf(subjectId) }
) {
  val notes by viewModel.notes.collectAsState()
  var editing by remember { mutableStateOf<Note?>(null) }

  Column(Modifier.fillMaxSize()) {
    if (notes.isEmpty()) {
      Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
          stringResource(R.string.no_notes),
          Modifier.testTag("no_notes"),
          color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
      }
    } else {
      LazyColumn(
        Modifier.weight(1f).fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = spacedBy(8.dp)
      ) {
        items(notes, key = { it.id }) { note ->
          NoteCard(note, onClick = { editing = note }) { viewModel.delete(note.id) }
        }
      }
    }
    NoteInput(viewModel::add)
  }

  editing?.let { note ->
    EditNoteDialog(
      note = note,
      onDismiss = { editing = null },
      onSave = { viewModel.update(note.id, it); editing = null },
      onDelete = { viewModel.delete(note.id); editing = null }
    )
  }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
  Card(Modifier.fillMaxWidth().clickable(onClick = onClick).testTag("note_card"), elevation = 2.dp) {
    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
      Column(Modifier.weight(1f), verticalArrangement = spacedBy(2.dp)) {
        Text(note.text, style = MaterialTheme.typography.body1)
        Text(
          note.creationDate.format(NoteDateFormat),
          style = MaterialTheme.typography.caption,
          color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
      }
      IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
      }
    }
  }
}

@Composable
private fun NoteInput(onAdd: (String) -> Unit) {
  var text by remember { mutableStateOf("") }
  Row(Modifier.fillMaxWidth().padding(8.dp), spacedBy(8.dp), Alignment.CenterVertically) {
    OutlinedTextField(
      value = text,
      onValueChange = { text = it },
      modifier = Modifier.weight(1f).testTag("new_note_field"),
      label = { Text(stringResource(R.string.new_note_hint)) },
      maxLines = 4
    )
    IconButton(
      onClick = { onAdd(text); text = "" },
      enabled = text.isNotBlank(),
      modifier = Modifier.testTag("add_note_button")
    ) {
      Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.add_note))
    }
  }
}

@Composable
private fun EditNoteDialog(note: Note, onDismiss: () -> Unit, onSave: (String) -> Unit, onDelete: () -> Unit) {
  var text by remember { mutableStateOf(note.text) }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.edit_note_title)) },
    text = {
      OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().testTag("edit_note_field"), maxLines = 6)
    },
    confirmButton = { TextButton(onClick = { onSave(text) }) { Text(stringResource(R.string.save)) } },
    dismissButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TextButton(onClick = onDelete) {
          Text(stringResource(R.string.delete), color = MaterialTheme.colors.error)
        }
        TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
      }
    }
  )
}
