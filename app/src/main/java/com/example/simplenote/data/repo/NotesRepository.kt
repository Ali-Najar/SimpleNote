// app/src/main/java/com/example/simplenote/data/repo/NotesRepository.kt
package com.example.simplenote.data.repo

import com.example.simplenote.data.remote.ApiClient
import com.example.simplenote.data.remote.dto.Note
import com.example.simplenote.data.remote.dto.NoteRequest
import com.example.simplenote.data.remote.dto.PaginatedNoteList

class NotesRepository {
    private val api = ApiClient.notesApi

    suspend fun list(page: Int, pageSize: Int): PaginatedNoteList = api.list(page, pageSize)
    suspend fun search(q: String, page: Int, pageSize: Int): PaginatedNoteList =
        api.filter(title = q, description = q, page = page, pageSize = pageSize)

    suspend fun get(id: Int): Note = api.get(id)
    suspend fun create(title: String, description: String): Note = api.create(NoteRequest(title, description))
    suspend fun update(id: Int, title: String, description: String): Note = api.update(id, NoteRequest(title, description))
    suspend fun delete(id: Int) = api.delete(id)
}
