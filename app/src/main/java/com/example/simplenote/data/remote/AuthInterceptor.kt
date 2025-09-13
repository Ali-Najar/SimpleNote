// app/src/main/java/com/example/simplenote/data/remote/AuthInterceptor.kt
package com.example.simplenote.data.remote

import android.content.Context
import com.example.simplenote.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val appContext: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val access = TokenStore.blockingAccess(appContext)
        val newReq = if (!access.isNullOrBlank()) {
            req.newBuilder()
                .addHeader("Authorization", "Bearer $access")
                .addHeader("Accept", "application/json")
                .build()
        } else req
        return chain.proceed(newReq)
    }
}
