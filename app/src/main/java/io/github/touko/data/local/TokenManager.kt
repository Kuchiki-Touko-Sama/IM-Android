package io.github.touko.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



object TokenManager {
    private const val KEY = "user_token"

    fun saveToken(token: String) {
        MMKV.defaultMMKV().encode(KEY, token)

    }

    fun getToken(): String? {
        return MMKV.defaultMMKV().decodeString(KEY)
    }
    fun clearToken() {
        MMKV.defaultMMKV().removeValueForKey(KEY)
    }

    /**
     * 是否已登录
     */
    fun isLogin(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}