// app/src/main/java/com/example/simplenote/ui/auth/LoginViewModel.kt
package com.example.simplenote.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    fun onUser(v: String) { _ui.value = _ui.value.copy(username = v) }
    fun onPass(v: String) { _ui.value = _ui.value.copy(password = v) }

    fun login() {
        val s = _ui.value
        if (s.username.isBlank() || s.password.isBlank()) {
            _ui.value = s.copy(error = "Username and password are required")
            return
        }
        _ui.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            val r = repo.login(s.username, s.password)
            _ui.value = if (r.isSuccess) {
                s.copy(loading = false, success = true)
            } else {
                s.copy(loading = false, error = r.exceptionOrNull()?.localizedMessage ?: "Login failed")
            }
        }
    }
}
