package io.github.touko.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.local.repository.MessageRepository
import io.github.touko.data.model.request.SendFriendApplyRequest
import io.github.touko.data.model.response.Friendship
import io.github.touko.data.model.response.FriendshipApply
import io.github.touko.data.model.response.LastMessage
import io.github.touko.data.model.response.TargetUser
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.remote.processNetworkError
import io.github.touko.data.remote.safeApiCall
import io.github.touko.feature.home.state.CurrentUserState
import io.github.touko.feature.home.state.FriendState
import io.github.touko.feature.home.ui.CurrentMainTab
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.NavigatorManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var errorMessage by mutableStateOf<String?>("")
    private val messageRepository = MessageRepository()

    var friendList by mutableStateOf<List<Friendship>>(emptyList())
        private set
    var personList by mutableStateOf<List<TargetUser>>(emptyList())
    var friendApplyList by mutableStateOf<List<FriendshipApply>>(emptyList())
    var currentMainTab by mutableStateOf(CurrentMainTab.ChatList)

    init {
        val localUID = LocalUserManager.getUid()
        if (localUID == 0)
            NavigatorManager.replace(LoginPage)
        CurrentUserState.login(localUID, LocalUserManager.getUsername())
        startSync()
        loadHistoryMessage()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val lastMessages: StateFlow<Map<Int, LastMessage>> = messageRepository.observeLastMessages(CurrentUserState.uid)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private var syncJob: Job? = null
    private fun startSync() {
        syncJob?.cancel()
        val uid = CurrentUserState.uid
        syncJob = viewModelScope.launch {
            while (isActive) {
                safeApiCall { HttpClient.friendApi.getFriendship(uid) }
                    .onSuccess { response ->
                        if (response.isSuccess())
                            friendList = response.data!!
                        for (friendship in response.data!!)
                            CurrentUserState.friendships[friendship.friendId] = FriendState.FRIEND
                    }
                    .onFailure { errorMessage = processNetworkError(it) }

                safeApiCall { HttpClient.friendApi.getFriendApply(uid) }
                    .onSuccess { response ->
                        if (response.isSuccess()) {
                            friendApplyList = response.data!!
                            for (apply in response.data)
                                CurrentUserState.friendships[apply.senderId] = FriendState.PENDING
                        }
                    }
                    .onFailure { errorMessage = processNetworkError(it) }
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
            errorMessage = null
            safeApiCall { HttpClient.userApi.getUsersByName(username) }
                .onSuccess { response ->
                    if (response.isSuccess())
                        personList = response.data!!.filter { it.userId != CurrentUserState.uid }
                    else
                        errorMessage = response.message
                }
                .onFailure { errorMessage = processNetworkError(it) }
        }
    }

    fun sendFriendshipApply(friendId: Int) {
        if (CurrentUserState.friendships[friendId] == FriendState.FRIEND) {
            errorMessage = "你们已经是好友"
            return
        }
        viewModelScope.launch {
            errorMessage = null
            safeApiCall { HttpClient.friendApi.applyFriend(SendFriendApplyRequest(CurrentUserState.uid, friendId)) }
                .onSuccess { response ->
                    if (response.isSuccess())
                        CurrentUserState.friendships[friendId] = FriendState.PENDING
                    else
                        errorMessage = response.message
                }
                .onFailure { errorMessage = processNetworkError(it) }
        }
    }

    fun acceptFriendApply(friendShipId: Int, senderId: Int) {
        viewModelScope.launch {
            errorMessage = null
            safeApiCall { HttpClient.friendApi.acceptFriendApply(friendShipId) }
                .onSuccess { response ->
                    if (response.isSuccess())
                        CurrentUserState.friendships[senderId] = FriendState.FRIEND
                    else
                        errorMessage = response.message
                }
                .onFailure { errorMessage = processNetworkError(it) }
        }
    }

    fun refuseFriendApply(friendshipId: Int, senderId: Int) {
        viewModelScope.launch {
            errorMessage = null
            safeApiCall { HttpClient.friendApi.refuseApply(friendshipId) }
                .onSuccess { response ->
                    if (response.isSuccess())
                        CurrentUserState.friendships[senderId] = FriendState.NONE
                    else
                        errorMessage = response.message
                }
                .onFailure { errorMessage = processNetworkError(it) }
        }
    }

    fun loadHistoryMessage() {
        viewModelScope.launch {
            messageRepository.fetchHistoryFromServer(CurrentUserState.uid)
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncJob?.cancel()
    }
}