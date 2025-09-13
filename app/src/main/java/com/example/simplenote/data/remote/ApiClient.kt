// app/src/main/java/com/example/simplenote/data/remote/ApiClient.kt
package com.example.simplenote.data.remote

import android.content.Context
import com.example.simplenote.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    @Volatile private var appContext: Context? = null

    fun init(context: Context) { appContext = context.applicationContext }

    private val http: OkHttpClient by lazy {
        val ctx = appContext ?: error("ApiClient.init(context) must be called before use")
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor(AuthInterceptor(ctx))
            .authenticator(TokenAuthenticator(ctx, BuildConfig.API_BASE_URL, json))
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(http)
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val notesApi: NotesApi by lazy { retrofit.create(NotesApi::class.java) }
}
