package io.github.touko.data.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object CurrentUserState {
    var uid by mutableIntStateOf(0)
    var username by mutableStateOf("")
}