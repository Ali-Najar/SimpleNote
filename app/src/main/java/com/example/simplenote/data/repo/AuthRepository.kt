// data/repo/AuthRepository.kt
package com.example.simplenote.data.repo
import android.content.Context
import com.example.simplenote.data.local.TokenStore
import com.example.simplenote.data.remote.dto.TokenRequest
import com.example.simplenote.data.remote.ApiClient

class AuthRepository(private val appContext: Context) {
    private val api = ApiClient.authApi

    suspend fun login(username: String, password: String): Result<Unit> = runCatching {
        val pair = api.login(TokenRequest(username, password))
        TokenStore.save(appContext, pair.access, pair.refresh)
    }

    suspend fun me(): Result<String> = runCatching {
        val access = TokenStore.getAccess(appContext) ?: error("No access token")
        val user = api.userInfo("Bearer $access")
        // return a friendly string or your own model
        "${user.username} (${user.email})"
    }
}
