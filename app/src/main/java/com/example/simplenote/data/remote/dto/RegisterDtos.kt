// app/src/main/java/com/example/simplenote/data/remote/dto/RegisterDtos.kt
package com.example.simplenote.data.remote.dto
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null
)

@Serializable
data class RegisterResponse(
    val username: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null
)
