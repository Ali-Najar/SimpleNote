// data/remote/AuthApi.kt
package com.example.simplenote.data.remote
import com.example.simplenote.data.remote.dto.*
import retrofit2.http.*

interface AuthApi {
    @POST("/api/auth/token/")
    suspend fun login(@Body body: TokenRequest): TokenPair

    @POST("/api/auth/token/refresh/")
    suspend fun refresh(@Body body: RefreshRequest): AccessOnly

    @GET("/api/auth/userinfo/")
    suspend fun userInfo(@Header("Authorization") bearer: String): UserInfo

    @POST("/api/auth/register/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("/api/auth/change-password/")
    suspend fun changePassword(@Body body: ChangePasswordRequest): Message // or any response type; not used
    @kotlinx.serialization.Serializable data class Message(val detail: String? = null)

    // app/src/main/java/com/example/simplenote/data/remote/AuthApi.kt
    @GET("/api/auth/userinfo/")
    suspend fun userinfo(): UserInfo

}
