package io.github.touko.feature.chat.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.R
import io.github.touko.feature.chat.ChatViewModel
import io.github.touko.feature.chat.ui.component.ChatInputBar
import io.github.touko.feature.chat.ui.component.ChatTopBar
import io.github.touko.feature.chat.ui.component.MessageList
import io.github.touko.feature.home.state.CurrentUserState

@Composable
fun ChatScreen(
    friendId: Int, friendName: String,
    viewModel: ChatViewModel = viewModel(key = "chat_${CurrentUserState.uid}_$friendId") {
        ChatViewModel(friendId)
    }
) {
    val messages by viewModel.messageList.collectAsStateWithLifecycle()
    Scaffold(topBar = { ChatTopBar(friendName, stringResource(R.string.status_online)) }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MessageList(
                messageList = messages,
                modifier = Modifier.padding(innerPadding)
            )
            ChatInputBar(
                onSendClick = {
                    viewModel.sendMessage()
                },
                onInputMessageChange = viewModel::updateInput,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomCenter),
                inputMessage = viewModel.inputMessage
            )
        }
    }
}
