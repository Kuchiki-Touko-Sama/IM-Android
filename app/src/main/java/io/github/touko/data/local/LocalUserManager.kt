package io.github.touko.data.local

import com.tencent.mmkv.MMKV
import io.github.touko.data.state.CurrentUserState

object LocalUserManager {
    private const val USERNAME_KEY = "username"
    private const val UID_KEY = "uid"

    fun changeCurrentUser(uid: Int, username: String) {
        MMKV.defaultMMKV().encode(USERNAME_KEY, username)
        MMKV.defaultMMKV().encode(UID_KEY, uid)
    }
    fun logout() {
        MMKV.defaultMMKV().clearAll()
        TokenManager.clearToken()
        CurrentUserState.logout()
    }
    fun getUid(): Int {
        return MMKV.defaultMMKV().decodeInt(UID_KEY)
    }
    fun getUsername(): String? {
        return MMKV.defaultMMKV().decodeString(USERNAME_KEY)
    }


}