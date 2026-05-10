package io.github.touko.data.state

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
}