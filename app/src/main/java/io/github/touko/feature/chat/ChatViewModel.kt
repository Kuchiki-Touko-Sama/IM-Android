package io.github.touko.feature.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.model.request.SendMessageRequest
import io.github.touko.data.model.response.Message
import io.github.touko.data.remote.ChatWebSocketManager
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.state.CurrentUserState
import kotlinx.coroutines.launch

class ChatViewModel(val friendId: Int) : ViewModel() {
    var messageList = mutableStateListOf<Message>()
    var inputMessage by mutableStateOf("")


    init {
        viewModelScope.launch {
            ChatWebSocketManager.messageFlow.collect { message ->
                onReceiveMessage(message)
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val response = HttpClient.messageApi.history(CurrentUserState.uid, friendId)
            if (response.code == 200 && response.data != null) {
                messageList.clear()
                messageList.addAll(response.data.filter {
                    it.senderId == friendId || it.receiverId == friendId
                })
            }
        }
    }

    fun syncMessage() {
        viewModelScope.launch {
            val lastMessageId =
                messageList.lastOrNull()?.messageId ?: 0
            val response = HttpClient.messageApi.sync(CurrentUserState.uid, lastMessageId)
            if (response.code == 200 && response.data != null) {
                val newMessage = response.data.filter {
                    it.senderId == friendId || it.receiverId == friendId
                }
                messageList += newMessage
            }
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

    fun onReceiveMessage(message: Message) {
        if ((message.senderId == CurrentUserState.uid && message.receiverId == friendId)
            || (message.senderId == friendId && message.receiverId == CurrentUserState.uid))
            messageList.add(message)
    }

    fun updateInput(text: String) {
        inputMessage = text
    }
}