// app/src/main/java/com/example/simplenote/data/remote/dto/NoteDtos.kt
package com.example.simplenote.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int,
    val title: String,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val creator_name: String? = null,
    val creator_username: String? = null
)

@Serializable
data class NoteRequest(val title: String, val description: String)

@Serializable
data class PaginatedNoteList(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Note>
)
