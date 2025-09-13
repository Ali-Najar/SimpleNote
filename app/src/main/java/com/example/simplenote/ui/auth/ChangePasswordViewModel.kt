// app/src/main/java/com/example/simplenote/ui/auth/ChangePasswordViewModel.kt
package com.example.simplenote.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.remote.ApiClient
import com.example.simplenote.data.remote.dto.ChangePasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ChangePwUi(
    val current: String = "",
    val new1: String = "",
    val new2: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class ChangePasswordViewModel : ViewModel() {
    private val api = ApiClient.authApi
    private val _ui = MutableStateFlow(ChangePwUi())
    val ui: StateFlow<ChangePwUi> = _ui

    fun onCurrent(v: String) { _ui.value = _ui.value.copy(current = v) }
    fun onNew1(v: String) { _ui.value = _ui.value.copy(new1 = v) }
    fun onNew2(v: String) { _ui.value = _ui.value.copy(new2 = v) }

    fun submit() {
        val s = _ui.value
        if (s.new1 != s.new2) { _ui.value = s.copy(error = "Passwords do not match"); return }
        if (s.new1.length < 8) { _ui.value = s.copy(error = "Password must be at least 8 characters"); return }
        _ui.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            val r = runCatching {
                api.changePassword(ChangePasswordRequest(old_password = s.current, new_password = s.new1))
            }
            if (r.isSuccess) _ui.value = s.copy(loading = false, success = true)
            else {
                val msg = (r.exceptionOrNull() as? HttpException)?.response()?.errorBody()?.string().orEmpty()
                _ui.value = s.copy(loading = false, error = msg.ifBlank { "Failed to change password" })
            }
        }
    }
}
