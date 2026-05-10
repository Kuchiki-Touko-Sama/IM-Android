package io.github.touko.ui.views.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.model.request.SendFriendApplyRequest
import io.github.touko.data.model.response.Friendship
import io.github.touko.data.model.response.FriendshipApply
import io.github.touko.data.model.response.TargetUser
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.state.CurrentUserState
import io.github.touko.data.state.FriendState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>("")
    var friendList by mutableStateOf<List<Friendship>>(emptyList())
    var personList by mutableStateOf<List<TargetUser>>(emptyList())
    var friendApplyList by mutableStateOf<List<FriendshipApply>>(emptyList())
    var currentMainTab by mutableStateOf(CurrentMainTab.FriendManager)

    private var syncJob: Job? = null

    init {
        startSync()
    }

    private fun startSync() {
        syncJob?.cancel()
        val uid = CurrentUserState.uid
        syncJob = viewModelScope.launch {
            while (isActive) {
                val response = HttpClient.friendApi.getFriendship(CurrentUserState.uid)
                if (response.code == 200 && response.data != null) {
                    friendList = response.data
                    for (friendship in response.data)
                        CurrentUserState.friendships[friendship.friendId] = FriendState.FRIEND
                }
                val responseOfFriendship = HttpClient.friendApi.getFriendshipApply(CurrentUserState.uid)
                if (responseOfFriendship.code == 200 && responseOfFriendship.data != null) {
                   friendApplyList  = responseOfFriendship.data
                }

                // 每10秒同步一次好友列表
                delay(10_000)
            }
        }
    }

    fun searchPeopleByName(username: String) {
        if (username.isEmpty()) {
            personList = emptyList()
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = HttpClient.userApi.getUsersByName(username)
            if (response.code == 200 && response.data != null) {
                personList = response.data
            }else {
                errorMessage = response.message
            }
            isLoading = false
        }
    }

    fun sendFriendshipApply(friendId: Int) {
        if (CurrentUserState.friendships[friendId] == FriendState.FRIEND){
            // TODO: 全局弹窗提示
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = HttpClient.friendApi.applyFriendship(SendFriendApplyRequest(
                CurrentUserState.uid, friendId))
            if (response.code == 200 && response.data != null) {
                CurrentUserState.friendships[friendId] = FriendState.PENDING
            } else {
                errorMessage = response.message
            }
            isLoading = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncJob?.cancel()
    }
}