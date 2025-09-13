// app/src/main/java/com/example/simplenote/data/remote/TokenAuthenticator.kt
package com.example.simplenote.data.remote

import android.content.Context
import com.example.simplenote.data.local.TokenStore
import com.example.simplenote.data.remote.dto.RefreshRequest
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit

class TokenAuthenticator(
    private val appContext: Context,
    private val baseUrl: String,
    private val json: Json
) : Authenticator {

    // Separate client/retrofit (no authenticator) to avoid recursion while refreshing
    private val plainClient = OkHttpClient()

    private val refreshApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(plainClient)
            .build()
            .create(AuthApi::class.java)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        // Stop after a couple of tries to avoid loops
        if (responseCount(response) >= 2) return null

        val refresh = TokenStore.blockingRefresh(appContext) ?: return null

        val newAccess = try {
            // ✅ Block here because Authenticator is synchronous
            val res = runBlocking {
                refreshApi.refresh(RefreshRequest(refresh))  // suspend → OK inside runBlocking
            }
            res.access
        } catch (_: HttpException) {
            return null
        } catch (_: Exception) {
            return null
        }

        // Save and retry original request with the new access token
        TokenStore.blockingSave(appContext, newAccess, null)
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var count = 1
        while (r?.priorResponse != null) { count++; r = r.priorResponse }
        return count
    }
}
