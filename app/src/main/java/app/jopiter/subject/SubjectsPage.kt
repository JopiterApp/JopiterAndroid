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
import androidx.compose.material.Text
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
import app.jopiter.subject.model.Subject
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubjectsPage(
  onAddSubject: () -> Unit,
  onEditSubject: (Long) -> Unit,
  viewModel: SubjectsViewModel = koinViewModel()
) {
  val subjects by viewModel.subjects.collectAsState()

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = onAddSubject, modifier = Modifier.testTag("add_subject_fab")) {
        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_subject))
      }
    }
  ) { padding ->
    if (subjects.isEmpty()) {
      Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.no_subjects), Modifier.testTag("no_subjects"))
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(subjects, key = { it.id }) { subject ->
          SubjectCard(subject) { onEditSubject(subject.id) }
        }
      }
    }
  }
}

@Composable
private fun SubjectCard(subject: Subject, onClick: () -> Unit) {
  Card(
    Modifier.fillMaxWidth().clickable(onClick = onClick).testTag("subject_card"),
    elevation = 2.dp
  ) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(subject.name, style = MaterialTheme.typography.h6)

      val subtitle = listOf(subject.code, subject.classroom, subject.lecturer)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
      if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.body2)

      subject.classTimes.forEach { classTime ->
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
    }
  }
}
