package io.github.touko.ui.views.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.model.response.Friendship
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.state.CurrentUserState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel : ViewModel() {
    var friendList by mutableStateOf<List<Friendship>>(emptyList())
    private var syncJob: Job? = null

    init {
        startSync()
    }

    private fun startSync() {
        syncJob?.cancel()
        val uid = CurrentUserState.uid
        syncJob = viewModelScope.launch {
            while (isActive) {
                val response =
                    HttpClient.friendApi.getFriendship(CurrentUserState.uid)
                if (response.code == 200 && response.data != null) {
                    friendList = response.data
                }
                // 每10秒同步一次好友列表
                delay(10_000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncJob?.cancel()
    }
}