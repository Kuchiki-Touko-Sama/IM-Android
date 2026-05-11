package io.github.touko.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.model.request.SendFriendApplyRequest
import io.github.touko.data.model.response.Friendship
import io.github.touko.data.model.response.FriendshipApply
import io.github.touko.data.model.response.TargetUser
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.state.CurrentUserState
import io.github.touko.data.state.FriendState
import io.github.touko.feature.home.ui.CurrentMainTab
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>("")
    var friendList by mutableStateOf<List<Friendship>>(emptyList())
    var personList by mutableStateOf<List<TargetUser>>(emptyList())
    var friendApplyList by mutableStateOf<List<FriendshipApply>>(emptyList())
    var currentMainTab by mutableStateOf(CurrentMainTab.ChatList)

    private var syncJob: Job? = null

    init {
        CurrentUserState.uid = LocalUserManager.getUid()
        CurrentUserState.username = LocalUserManager.getUsername().toString()
        startSync()
    }

    private fun startSync() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            while (isActive) {
                val uid = CurrentUserState.uid
                if (uid == 0) {
                    delay(1000)
                    continue
                }
                val response = HttpClient.friendApi.getFriendship(uid)
                if (response.code == 200 && response.data != null) {
                    friendList = response.data
                    for (friendship in response.data)
                        CurrentUserState.friendships[friendship.friendId] = FriendState.FRIEND
                }
                val responseOfFriendship = HttpClient.friendApi.getFriendApply(uid)
                if (responseOfFriendship.code == 200 && responseOfFriendship.data != null) {
                   friendApplyList  = responseOfFriendship.data
                    for (apply in responseOfFriendship.data) {
                        CurrentUserState.friendships[apply.senderId] = FriendState.PENDING
                    }
                }
                delay(1000)
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
            val response = HttpClient.friendApi.applyFriend(
                SendFriendApplyRequest(
                    CurrentUserState.uid, friendId
                )
            )
            if (response.code == 200 && response.data != null) {
                CurrentUserState.friendships[friendId] = FriendState.PENDING
            } else {
                errorMessage = response.message
            }
            isLoading = false
        }
    }

    fun acceptFriendApply(friendShipId: Int, senderId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = HttpClient.friendApi.acceptFriendApply(friendShipId)
            if (response.code == 200 && response.data != null) {
                CurrentUserState.friendships[senderId] = FriendState.FRIEND
                // TODO: 提示成功添加
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