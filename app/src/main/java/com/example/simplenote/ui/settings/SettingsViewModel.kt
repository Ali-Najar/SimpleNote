// app/src/main/java/com/example/simplenote/ui/settings/SettingsViewModel.kt
package com.example.simplenote.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.local.TokenStore
import com.example.simplenote.data.remote.ApiClient
import com.example.simplenote.data.remote.dto.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUi(
    val user: UserInfo? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val api = ApiClient.authApi
    private val _ui = MutableStateFlow(SettingsUi(loading = true))
    val ui: StateFlow<SettingsUi> = _ui

    init { refresh() }

    fun refresh() {
        _ui.value = _ui.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { api.userinfo() }
                .onSuccess { _ui.value = _ui.value.copy(user = it, loading = false) }
                .onFailure { _ui.value = _ui.value.copy(loading = false, error = it.localizedMessage ?: "Failed to load profile") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            TokenStore.clear(getApplication())
            _ui.value = _ui.value.copy(loggedOut = true)
        }
    }
}
