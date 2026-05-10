package io.github.touko.ui.views.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.ui.component.ChatInputBar
import io.github.touko.ui.component.ChatTopBar
import io.github.touko.ui.component.MessageList

@Composable
fun ChatScreen(userId: Int, userName: String, viewModel: ChatViewModel = viewModel()) {
    // 初始化组件前全量拉取历史消息

    LaunchedEffect(userId) {
        viewModel.loadHistory(userId)
    }
    //viewModel.syncMessage(userId)

    Scaffold(
        topBar = { ChatTopBar(userName, "online") }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MessageList(
                messageList = viewModel.messageList,
                modifier = Modifier.padding(innerPadding)
            )
            ChatInputBar(
                onSendClick = {
                    viewModel.sendMessage(userId)
                },
                onInputMessageChange = viewModel::updateInput,
                modifier = Modifier.padding(10.dp).align(Alignment.BottomCenter),
                inputMessage = viewModel.inputMessage
            )
        }
    }
}
