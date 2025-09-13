// app/src/main/java/com/example/simplenote/ui/auth/RegisterViewModel.kt
package com.example.simplenote.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.remote.ApiClient
import com.example.simplenote.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull // ✅ needed for contentOrNull
import retrofit2.HttpException

data class RegisterUi(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null,                                // general/top error
    val fieldErrors: Map<String, List<String>> = emptyMap(),  // per-field errors
    val success: Boolean = false
)

class RegisterViewModel(app: Application) : AndroidViewModel(app) {
    private val api = ApiClient.authApi

    private val _ui = MutableStateFlow(RegisterUi())
    val ui: StateFlow<RegisterUi> = _ui

    fun onFirst(v: String)   { _ui.value = _ui.value.copy(firstName = v) }
    fun onLast(v: String)    { _ui.value = _ui.value.copy(lastName  = v) }
    fun onUser(v: String)    { _ui.value = _ui.value.copy(username  = v) }
    fun onEmail(v: String)   { _ui.value = _ui.value.copy(email     = v) }
    fun onPass(v: String)    { _ui.value = _ui.value.copy(password  = v) }
    fun onConfirm(v: String) { _ui.value = _ui.value.copy(confirm   = v) }

    fun register(done: (Boolean) -> Unit) {
        val s = _ui.value

        // Client-side checks → inline field errors
        val local = buildMap<String, MutableList<String>> {
            if (s.username.isBlank()) put("username", mutableListOf("Username is required"))
            if (s.email.isBlank())    put("email",    mutableListOf("Email is required"))
            if (s.password.isBlank()) put("password", mutableListOf("Password is required"))
            if (s.password != s.confirm) put("confirm", mutableListOf("Passwords do not match"))
            if (s.password.length < 8) getOrPut("password") { mutableListOf() }.add("Password must be at least 8 characters")
        }
        if (local.isNotEmpty()) {
            _ui.value = s.copy(error = "Please fix the highlighted fields.", fieldErrors = local, success = false)
            return
        }

        _ui.value = s.copy(loading = true, error = null, fieldErrors = emptyMap(), success = false)
        viewModelScope.launch {
            val result = runCatching {
                api.register(
                    RegisterRequest(
                        username   = s.username,
                        password   = s.password,
                        email      = s.email,
                        first_name = s.firstName.ifBlank { null },
                        last_name  = s.lastName.ifBlank { null }
                    )
                )
            }

            if (result.isSuccess) {
                _ui.value = s.copy(loading = false, error = null, fieldErrors = emptyMap(), success = true)
                done(true)
            } else {
                val ex = result.exceptionOrNull()
                val body = (ex as? HttpException)?.response()?.errorBody()?.string().orEmpty()
                val parsed = parseApiError(body)

                val generalMsg = when {
                    parsed.generalMessages.isNotEmpty() -> parsed.generalMessages.joinToString("\n")
                    parsed.fieldErrors.isNotEmpty() ->
                        parsed.fieldErrors.entries.joinToString("\n") { (k, v) -> "$k: ${v.joinToString(", ")}" }
                    else -> ex?.localizedMessage ?: "Registration failed"
                }

                _ui.value = s.copy(
                    loading = false,
                    error = generalMsg,
                    fieldErrors = parsed.fieldErrors,
                    success = false
                )
                done(false)
            }
        }
    }

    // ---------- Error parsing ----------

    private data class ParsedError(
        val generalMessages: List<String> = emptyList(),
        val fieldErrors: Map<String, List<String>> = emptyMap()
    )

    /**
     * Supports:
     * 1) { "errors": [ { "detail": "...", "attr": "password" }, ... ] }
     * 2) { "password": ["Too short"], "email": ["Already taken"] }
     */
    private fun parseApiError(body: String): ParsedError {
        if (body.isBlank()) return ParsedError()
        return try {
            val elt = Json { ignoreUnknownKeys = true }.parseToJsonElement(body)

            // Format 1: documented shape with "errors"
            if (elt is JsonObject && elt["errors"] is JsonArray) {
                val arr = elt["errors"]!!.jsonArray
                val general = mutableListOf<String>()
                val perField = mutableMapOf<String, MutableList<String>>()
                for (e in arr) {
                    val obj = e.jsonObject
                    val detail = obj["detail"]?.jsonPrimitive?.contentOrNull
                    val attr = obj["attr"]?.jsonPrimitive?.contentOrNull
                    if (!attr.isNullOrBlank() && !detail.isNullOrBlank()) {
                        perField.getOrPut(attr) { mutableListOf() }.add(detail)
                    } else if (!detail.isNullOrBlank()) {
                        general.add(detail)
                    }
                }
                return ParsedError(general, perField)
            }

            // Format 2: DRF default per-field JSON
            if (elt is JsonObject) {
                val perField = mutableMapOf<String, MutableList<String>>()
                val general = mutableListOf<String>()
                for ((k, v) in elt) {
                    when (v) {
                        is JsonArray -> {
                            val msgs = v.mapNotNull { it.jsonPrimitive.contentOrNull }.filter { it.isNotBlank() }
                            if (msgs.isNotEmpty()) perField.getOrPut(k) { mutableListOf() }.addAll(msgs)
                        }
                        is JsonPrimitive -> {
                            val msg = v.contentOrNull
                            if (!msg.isNullOrBlank()) perField.getOrPut(k) { mutableListOf() }.add(msg)
                        }
                        is JsonObject -> {
                            general.add("$k: ${v.toString()}")
                        }
                    }
                }
                return ParsedError(general, perField)
            }

            // Fallback
            ParsedError(generalMessages = listOf(body))
        } catch (_: Exception) {
            ParsedError(generalMessages = listOf(body))
        }
    }
}

// Optional reference types matching your docs
@Serializable
data class ApiErrorItem(val attr: String? = null, val code: String? = null, val detail: String? = null)

@Serializable
data class ApiErrorResponse(val type: String? = null, val errors: List<ApiErrorItem>? = null)
