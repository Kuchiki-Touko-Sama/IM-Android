package io.github.touko.data.local

import android.content.Context
import android.content.Intent
import com.tencent.mmkv.MMKV
import io.github.touko.App
import io.github.touko.MainActivity
import io.github.touko.data.remote.ChatWebSocketManager
import io.github.touko.feature.home.state.CurrentUserState
import io.github.touko.navigation.NavigatorManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object LocalUserManager {
    private const val USERNAME_KEY = "username"
    private const val UID_KEY = "uid"

    fun changeCurrentUser(uid: Int, username: String) {
        MMKV.defaultMMKV().encode(USERNAME_KEY, username)
        MMKV.defaultMMKV().encode(UID_KEY, uid)
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun logout(context: Context) {
        //1. 清理持久化状态
        MMKV.defaultMMKV().clearAll()
        //2. 清理内存状态
        CurrentUserState.logout()
        ChatWebSocketManager.disconnect()
        NavigatorManager.backStack.clear()
        //3. 异步清理本地数据库
        GlobalScope.launch(Dispatchers.IO) {
            App.db.clearAllTables()
        }
        //4. 重建 Activity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
    fun getUid(): Int {
        return MMKV.defaultMMKV().decodeInt(UID_KEY)
    }
    fun getUsername(): String? {
        return MMKV.defaultMMKV().decodeString(USERNAME_KEY)
    }
}