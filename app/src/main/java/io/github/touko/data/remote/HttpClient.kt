package io.github.touko.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.github.touko.App
import io.github.touko.data.local.TokenManager
import io.github.touko.data.remote.api.FriendApi
import io.github.touko.data.remote.api.MessageApi
import io.github.touko.data.remote.api.UserApi
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.NavigatorManager
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException

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
        .addInterceptor(GlobalInterceptor(App.instance))
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

class GlobalInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkConnected(context)) {
            // 直接抛出异常，请求不会发出去
            throw NoNetworkException()
        }
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

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        if (!response.isSuccessful) {
            when (response.code) {
                401 -> {
                    TokenManager.clearToken()
                    NavigatorManager.replace(LoginPage)
                }
            }
        }
        return response
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}


class NoNetworkException(message: String = "当前无网络连接") : IOException(message)