package io.github.touko.feature.home.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class FriendState {
    PENDING,
    FRIEND,
    NONE
}

object CurrentUserState {
    var uid by mutableIntStateOf(0)
    var username by mutableStateOf("")
    var friendships = mutableStateMapOf<Int, FriendState>()

    fun login(uid: Int, username: String) {
        // 切换用户
        if (this.uid != uid) {
            friendships.clear()
        }
        this.uid = uid
        this.username = username
    }

    fun logout() {
        uid = 0
        username = ""
        friendships.clear()
    }
}