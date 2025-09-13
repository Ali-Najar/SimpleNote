// app/src/main/java/com/example/simplenote/data/remote/NotesApi.kt
package com.example.simplenote.data.remote

import com.example.simplenote.data.remote.dto.*
import retrofit2.http.*

interface NotesApi {
    @GET("/api/notes/")
    suspend fun list(@Query("page") page: Int, @Query("page_size") pageSize: Int): PaginatedNoteList

    @GET("/api/notes/filter")
    suspend fun filter(
        @Query("title") title: String? = null,
        @Query("description") description: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): PaginatedNoteList

    @POST("/api/notes/")
    suspend fun create(@Body body: NoteRequest): Note

    @GET("/api/notes/{id}/")
    suspend fun get(@Path("id") id: Int): Note

    @PUT("/api/notes/{id}/")
    suspend fun update(@Path("id") id: Int, @Body body: NoteRequest): Note

    @DELETE("/api/notes/{id}/")
    suspend fun delete(@Path("id") id: Int)
}
