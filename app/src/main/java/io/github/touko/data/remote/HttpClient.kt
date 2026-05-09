package io.github.touko.data.remote

import io.github.touko.data.remote.api.UserApi
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
        .addInterceptor(ErrorInterceptor())
        .build()

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UserApi::class.java)
    }
}

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (!response.isSuccessful) {
            when (response.code) {
                401 -> { /* 发送全局 EventBus 或跳转登录 */ }
                else -> { /* 抛出自定义网络异常 */ }
            }
        }

        return response
    }
}