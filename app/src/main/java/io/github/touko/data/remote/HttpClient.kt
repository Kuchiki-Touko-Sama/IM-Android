package io.github.touko.data.remote

import android.util.Log
import io.github.touko.data.local.TokenManager
import io.github.touko.data.remote.api.FriendApi
import io.github.touko.data.remote.api.MessageApi
import io.github.touko.data.remote.api.UserApi
import io.github.touko.navigation.NavigatorState
import io.github.touko.event.GlobalEvent
import io.github.touko.navigation.LoginPage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object HttpClient {
    private const val BASE_URL = "http://192.168.31.224:8080/api/"
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(GlobalInterceptor())
        .build()

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UserApi::class.java)
    }
    val friendApi: FriendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FriendApi::class.java)
    }

    val messageApi: MessageApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MessageApi::class.java)
    }
}

class GlobalInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken()
        val request = chain.request()
            .newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader(
                        "Authorization",
                        token
                    )
                }
            }
            .build()
        Log.d("im-http", "intercept: $request")
        val response = chain.proceed(request)
        if (!response.isSuccessful) {
            when (response.code) {
                401 -> {
                    TokenManager.clearToken()
                    NavigatorState.replace(LoginPage)
                }
                else -> { /* 抛出自定义网络异常 */ }
            }
        }
        return response
    }
}