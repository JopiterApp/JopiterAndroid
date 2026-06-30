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
import app.jopiter.subject.model.ClassTime
import app.jopiter.subject.model.Subject
import app.jopiter.subject.repository.SubjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Validation problems on the subject name. */
enum class SubjectNameError { Blank, Duplicate }

/** Editable state of the subject form. [maxMissedClasses] is kept as text to mirror the input field. */
data class SubjectFormState(
  val id: Long = 0,
  val name: String = "",
  val code: String = "",
  val classroom: String = "",
  val lecturer: String = "",
  val lecturerEmail: String = "",
  val maxMissedClasses: String = "0",
  val classTimes: List<ClassTime> = emptyList(),
  val nameError: SubjectNameError? = null,
  val classTimesError: Boolean = false
)

/**
 * Drives the create/edit subject form. Mirrors the legacy validation: the name must be non-blank and
 * unique, and a subject must have at least one class time. [subjectId] of 0 means a new subject.
 */
class SubjectEditViewModel(
  private val repository: SubjectRepository,
  private val subjectId: Long
) : ViewModel() {

  private val _state = MutableStateFlow(SubjectFormState(id = subjectId))
  val state: StateFlow<SubjectFormState> = _state

  init {
    if (subjectId != 0L) {
      repository.findById(subjectId)?.let { subject ->
        _state.update { it.from(subject) }
      }
    }
  }

  fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }
  fun onCodeChange(value: String) = _state.update { it.copy(code = value) }
  fun onClassroomChange(value: String) = _state.update { it.copy(classroom = value) }
  fun onLecturerChange(value: String) = _state.update { it.copy(lecturer = value) }
  fun onLecturerEmailChange(value: String) = _state.update { it.copy(lecturerEmail = value) }
  fun onMaxMissedChange(value: String) = _state.update { it.copy(maxMissedClasses = value.filter(Char::isDigit)) }

  fun addClassTime(classTime: ClassTime) =
    _state.update { it.copy(classTimes = it.classTimes + classTime, classTimesError = false) }

  fun removeClassTime(index: Int) =
    _state.update { it.copy(classTimes = it.classTimes.filterIndexed { i, _ -> i != index }) }

  /** Validates and persists. Returns true when saved; otherwise updates [state] with the errors. */
  fun save(): Boolean {
    val current = _state.value
    val name = current.name.trim()
    val nameError = when {
      name.isBlank() -> SubjectNameError.Blank
      repository.isNameTaken(name, current.id) -> SubjectNameError.Duplicate
      else -> null
    }
    val classTimesError = current.classTimes.isEmpty()

    if (nameError != null || classTimesError) {
      _state.update { it.copy(nameError = nameError, classTimesError = classTimesError) }
      return false
    }

    repository.save(current.toSubject())
    return true
  }
}

private fun SubjectFormState.from(subject: Subject) = copy(
  id = subject.id,
  name = subject.name,
  code = subject.code,
  classroom = subject.classroom,
  lecturer = subject.lecturer,
  lecturerEmail = subject.lecturerEmail,
  maxMissedClasses = subject.maxMissedClasses.toString(),
  classTimes = subject.classTimes
)

private fun SubjectFormState.toSubject() = Subject(
  id = id,
  name = name.trim(),
  code = code.trim(),
  classroom = classroom.trim(),
  lecturer = lecturer.trim(),
  lecturerEmail = lecturerEmail.trim(),
  maxMissedClasses = maxMissedClasses.toIntOrNull() ?: 0,
  classTimes = classTimes
)
