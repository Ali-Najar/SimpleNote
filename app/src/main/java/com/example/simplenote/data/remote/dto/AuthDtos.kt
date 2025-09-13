package com.example.simplenote.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenRequest(val username: String, val password: String)

@Serializable
data class TokenPair(val access: String, val refresh: String)

@Serializable
data class RefreshRequest(val refresh: String)

@Serializable
data class AccessOnly(val access: String)

@Serializable
data class ChangePasswordRequest(val old_password: String, val new_password: String)

/** Many endpoints (e.g., change-password) return { "detail": "..." } */
@Serializable
data class Message(val detail: String? = null)

/** /api/auth/userinfo/  — make names nullable to be robust */
@Serializable
data class UserInfo(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null
)
