// app/src/main/java/com/example/simplenote/data/local/TokenStore.kt
package com.example.simplenote.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore("auth_store")

object TokenStore {
    private val KEY_ACCESS = stringPreferencesKey("access")
    private val KEY_REFRESH = stringPreferencesKey("refresh")

    suspend fun save(ctx: Context, access: String, refresh: String) {
        ctx.dataStore.edit { it[KEY_ACCESS] = access; it[KEY_REFRESH] = refresh }
    }
    suspend fun getAccess(ctx: Context): String? = ctx.dataStore.data.first()[KEY_ACCESS]
    suspend fun getRefresh(ctx: Context): String? = ctx.dataStore.data.first()[KEY_REFRESH]
    suspend fun clear(ctx: Context) { ctx.dataStore.edit { it.clear() } }

    // ✅ blocking helpers for OkHttp Authenticator
    fun blockingAccess(ctx: Context): String? = runBlocking { getAccess(ctx) }
    fun blockingRefresh(ctx: Context): String? = runBlocking { getRefresh(ctx) }
    fun blockingSave(ctx: Context, access: String, refresh: String?) = runBlocking {
        ctx.dataStore.edit {
            it[KEY_ACCESS] = access
            if (refresh != null) it[KEY_REFRESH] = refresh
        }
    }
}
