package com.example.simplenote.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.repo.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class EditorUi(
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val lastEdited: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val deleted: Boolean = false,
    val saved: Boolean = false
)

class NoteEditorViewModel : ViewModel() {
    private val repo = NotesRepository()
    private val _ui = MutableStateFlow(EditorUi())
    val ui: StateFlow<EditorUi> = _ui

    fun load(id: Int?) {
        if (id == null) { _ui.value = EditorUi(); return }
        _ui.value = _ui.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.get(id) }.onSuccess {
                _ui.value = EditorUi(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    lastEdited = it.updated_at,
                    loading = false
                )
            }.onFailure {
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = it.localizedMessage ?: "Failed to load"
                )
            }
        }
    }

    fun onTitle(v: String) { _ui.value = _ui.value.copy(title = v, saved = false) }
    fun onDesc(v: String)  { _ui.value = _ui.value.copy(description = v, saved = false) }

    fun save() {
        val s = _ui.value
        if (s.title.isBlank()) {
            _ui.value = s.copy(error = "Title is required")
            return
        }
        _ui.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            val r = if (s.id == null)
                runCatching { repo.create(s.title, s.description) }
            else
                runCatching { repo.update(s.id, s.title, s.description) }

            r.onSuccess {
                _ui.value = _ui.value.copy(
                    id = it.id,
                    lastEdited = it.updated_at,
                    loading = false,
                    saved = true
                )
            }.onFailure { e ->
                val http = e as? HttpException
                val body = http?.response()?.errorBody()?.string().orEmpty()
                val msg = buildString {
                    append("Save failed")
                    http?.code()?.let { c -> append(" ($c)") }
                    if (body.isNotBlank()) append(": ").append(body)
                }
                _ui.value = _ui.value.copy(loading = false, error = msg)
            }
        }
    }

    fun delete() {
        val id = _ui.value.id ?: run {
            _ui.value = _ui.value.copy(error = "Nothing to delete")
            return
        }
        _ui.value = _ui.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val r = runCatching { repo.delete(id) }
            r.onSuccess {
                _ui.value = _ui.value.copy(loading = false, deleted = true)
            }.onFailure { e ->
                val http = e as? HttpException
                val body = http?.response()?.errorBody()?.string().orEmpty()
                val msg = buildString {
                    append("Delete failed")
                    http?.code()?.let { c -> append(" ($c)") }
                    if (body.isNotBlank()) append(": ").append(body)
                }
                _ui.value = _ui.value.copy(loading = false, error = msg)
            }
        }
    }
}
