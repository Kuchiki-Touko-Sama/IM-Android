package io.github.touko.feature.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.App
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.model.request.SendFriendApplyRequest
import io.github.touko.data.model.response.Friendship
import io.github.touko.data.model.response.FriendshipApply
import io.github.touko.data.model.response.LastMessage
import io.github.touko.data.model.response.TargetUser
import io.github.touko.data.remote.HttpClient
import io.github.touko.feature.home.state.CurrentUserState
import io.github.touko.feature.home.state.FriendState
import io.github.touko.feature.home.ui.CurrentMainTab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>("")


    private val _friendListFlow = MutableStateFlow<List<Friendship>>(emptyList())
    var friendList by mutableStateOf<List<Friendship>>(emptyList())
        private set

    var personList by mutableStateOf<List<TargetUser>>(emptyList())
    var friendApplyList by mutableStateOf<List<FriendshipApply>>(emptyList())
    var currentMainTab by mutableStateOf(CurrentMainTab.ChatList)

    private val messageDao by lazy { App.db.messageDao() }
    private var syncJob: Job? = null

    init {
        CurrentUserState.login(LocalUserManager.getUid(), LocalUserManager.getUsername())
        startSync()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val lastMessages: StateFlow<Map<Int, LastMessage>> = _friendListFlow
        // 根据friendList变化进行最新消息同步
        .flatMapLatest { friends ->
            val uid = CurrentUserState.uid
            if (uid == 0 || friends.isEmpty()) {
                flowOf(emptyList())
            } else {
                val friendIds = friends.map { it.friendId }
                Log.d("lastMsg", friendIds.toString())
                // 监听 Room，数据库一变这边自动发射新数据
                messageDao.getLastMessagesForFriends(uid, friendIds)
            }
        }
        .map { entities ->
            // 把 Room 实体类映射为 UI 需要的 Model 结构
            entities.associate { entity ->
                entity.friendId to LastMessage(
                    friendId = entity.friendId,
                    content = entity.content,
                    lastMessageTime = entity.lastMessageTime
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 界面可见时才激活监听
            initialValue = emptyMap()
        )

    private fun startSync() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            while (isActive) {
                val uid = CurrentUserState.uid
                if (uid == 0) {
                    delay(1000)
                    continue
                }
                try {
                    // 1. 同步好友列表
                    val response = HttpClient.friendApi.getFriendship(uid)
                    if (response.code == 200 && response.data != null) {
                        val newData = response.data
                        friendList = newData
                        _friendListFlow.value = newData // 触发 lastMessages 链条更新
                        for (friendship in newData)
                            CurrentUserState.friendships[friendship.friendId] = FriendState.FRIEND
                    }

                    // 2. 同步好友申请
                    val responseOfFriendship = HttpClient.friendApi.getFriendApply(uid)
                    if (responseOfFriendship.code == 200 && responseOfFriendship.data != null) {
                        friendApplyList = responseOfFriendship.data
                        for (apply in responseOfFriendship.data) {
                            CurrentUserState.friendships[apply.senderId] = FriendState.PENDING
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "轮询同步", e)
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
                    CurrentUserState.uid,
                    friendId
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

    fun refuseFriendApply(friendshipId: Int, senderId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = HttpClient.friendApi.refuseApply(friendshipId)
            if (response.code == 200 && response.data != null) {
                CurrentUserState.friendships[senderId] = FriendState.NONE
            }else {
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