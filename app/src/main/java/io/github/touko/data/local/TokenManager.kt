package io.github.touko.data.local

import com.tencent.mmkv.MMKV

object TokenManager {
    private const val KEY = "user_token"

    fun saveToken(token: String) = MMKV.defaultMMKV().encode(KEY, token)

    fun getToken(): String? = MMKV.defaultMMKV().decodeString(KEY)
    fun clearToken() = MMKV.defaultMMKV().removeValueForKey(KEY)
    fun isLogin(): Boolean = !getToken().isNullOrEmpty()

}