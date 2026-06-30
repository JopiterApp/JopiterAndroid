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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.jopiter.R
import app.jopiter.common.TimeFormat
import app.jopiter.common.displayNameRes
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubjectsPage(
  onAddSubject: () -> Unit,
  onEditSubject: (Long) -> Unit,
  onOpenNotes: (Long) -> Unit,
  viewModel: SubjectsViewModel = koinViewModel()
) {
  val summaries by viewModel.summaries.collectAsState()

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = onAddSubject, modifier = Modifier.testTag("add_subject_fab")) {
        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_subject))
      }
    }
  ) { padding ->
    if (summaries.isEmpty()) {
      Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.no_subjects), Modifier.testTag("no_subjects"))
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(summaries, key = { it.subject.id }) { summary ->
          SubjectCard(
            summary = summary,
            onClick = { onEditSubject(summary.subject.id) },
            onTogglePresence = { viewModel.toggleTodayPresence(summary.subject.id) },
            onOpenNotes = { onOpenNotes(summary.subject.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun SubjectCard(
  summary: SubjectSummary,
  onClick: () -> Unit,
  onTogglePresence: () -> Unit,
  onOpenNotes: () -> Unit
) {
  Card(
    Modifier.fillMaxWidth().clickable(onClick = onClick).testTag("subject_card"),
    elevation = 2.dp
  ) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(summary.subject.name, style = MaterialTheme.typography.h6)

      val subtitle = listOf(summary.subject.code, summary.subject.classroom, summary.subject.lecturer)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
      if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.body2)

      summary.subject.classTimes.forEach { classTime ->
        Text(
          stringResource(
            R.string.class_time_summary,
            stringResource(classTime.dayOfWeek.displayNameRes),
            classTime.startAt.format(TimeFormat),
            classTime.endAt.format(TimeFormat)
          ),
          style = MaterialTheme.typography.body2
        )
      }

      AbsencesText(summary.missed, summary.subject.maxMissedClasses, summary.remaining)
      if (summary.hasClassToday) PresenceToggle(summary.presentToday, onTogglePresence)
      TextButton(onClick = onOpenNotes, modifier = Modifier.testTag("notes_button")) {
        Text(stringResource(R.string.notes_button))
      }
    }
  }
}

@Composable
private fun AbsencesText(missed: Int, maxMissed: Int, remaining: Int) {
  Text(
    stringResource(R.string.absences_format, missed, maxMissed),
    color = if (remaining <= 0) MaterialTheme.colors.error else MaterialTheme.colors.primary,
    style = MaterialTheme.typography.subtitle2,
    modifier = Modifier.testTag("absences")
  )
}

@Composable
private fun PresenceToggle(presentToday: Boolean, onTogglePresence: () -> Unit) {
  Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
    Text(stringResource(R.string.present_today), style = MaterialTheme.typography.body2)
    Switch(
      checked = presentToday,
      onCheckedChange = { onTogglePresence() },
      modifier = Modifier.testTag("presence_toggle")
    )
  }
}
