package io.github.touko.feature.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.local.mapper.toEntity
import io.github.touko.data.local.repository.ChatRepository
import io.github.touko.data.model.request.SendMessageRequest
import io.github.touko.data.model.response.Message
import io.github.touko.data.remote.ChatWebSocketManager
import io.github.touko.feature.home.state.CurrentUserState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(val friendId: Int) : ViewModel() {

    var inputMessage by mutableStateOf("")
    val chatRepository = ChatRepository()
    var messageList = chatRepository.observeMessages(CurrentUserState.uid, friendId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            // 接收websocket 回显的消息
            ChatWebSocketManager.messageFlow.collect { message ->
                onReceiveMessage(message)
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            chatRepository.fetchHistoryFromServer(CurrentUserState.uid, friendId)
        }
    }

    fun sendMessage() {
        if (inputMessage.isBlank())
            return
        inputMessage = inputMessage.trim()
        val message = SendMessageRequest(
            senderId = CurrentUserState.uid,
            receiverId = friendId,
            content = inputMessage,
        )
        ChatWebSocketManager.send(message)
        inputMessage = ""
    }

    suspend fun onReceiveMessage(message: Message) {
        if ((message.senderId == CurrentUserState.uid && message.receiverId == friendId)
            || (message.senderId == friendId && message.receiverId == CurrentUserState.uid)
        )
            chatRepository.saveMessage(message.toEntity())
    }

    fun updateInput(text: String) {
        inputMessage = text
    }
}