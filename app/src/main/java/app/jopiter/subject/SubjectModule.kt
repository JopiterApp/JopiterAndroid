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

import app.jopiter.Database
import app.jopiter.subject.repository.PresenceRepository
import app.jopiter.subject.repository.SubjectRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val subjectModule = module {
  single { SubjectRepository(get<Database>().subjectQueries, get<Database>().classTimeQueries) }
  single { PresenceRepository(get<Database>().presenceQueries) }
  viewModel { SubjectsViewModel(get(), get()) }
  viewModel { parameters -> SubjectEditViewModel(get(), parameters.get()) }
}

/** Convenience for screens to obtain a [SubjectEditViewModel] bound to a subject id. */
fun subjectEditParameters(subjectId: Long) = parametersOf(subjectId)
