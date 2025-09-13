// app/src/main/java/com/example/simplenote/ui/home/HomeViewModel.kt
package com.example.simplenote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.remote.dto.Note
import com.example.simplenote.data.repo.NotesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUi(
    val notes: List<Note> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val page: Int = 1,
    val endReached: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val repo = NotesRepository()
    private val _ui = MutableStateFlow(HomeUi())
    val ui: StateFlow<HomeUi> = _ui

    private var searchJob: Job? = null
    private val pageSize = 20

    init { refresh() }

    fun refresh() {
        _ui.value = _ui.value.copy(loading = true, error = null, page = 1, endReached = false, notes = emptyList())
        viewModelScope.launch {
            runCatching {
                if (_ui.value.query.isBlank()) repo.list(1, pageSize)
                else repo.search(_ui.value.query, 1, pageSize)
            }.onSuccess {
                _ui.value = _ui.value.copy(notes = it.results, loading = false, page = 1, endReached = it.next == null)
            }.onFailure {
                _ui.value = _ui.value.copy(loading = false, error = it.localizedMessage ?: "Failed to load notes")
            }
        }
    }

    fun loadMore() {
        val s = _ui.value
        if (s.loading || s.endReached) return
        _ui.value = s.copy(loading = true, error = null)
        val nextPage = s.page + 1
        viewModelScope.launch {
            runCatching {
                if (s.query.isBlank()) repo.list(nextPage, pageSize)
                else repo.search(s.query, nextPage, pageSize)
            }.onSuccess {
                _ui.value = _ui.value.copy(
                    notes = _ui.value.notes + it.results,
                    loading = false, page = nextPage, endReached = it.next == null
                )
            }.onFailure {
                _ui.value = _ui.value.copy(loading = false, error = it.localizedMessage ?: "Failed to load more")
            }
        }
    }

    fun onQuery(q: String) {
        _ui.value = _ui.value.copy(query = q)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            refresh()
        }
    }
}
