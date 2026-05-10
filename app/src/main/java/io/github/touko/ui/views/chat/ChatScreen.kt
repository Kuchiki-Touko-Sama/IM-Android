package io.github.touko.ui.views.chat

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.ui.views.home.MainViewModel

@Composable
fun ChatScreen(userId: Int, userName: String, viewModel: ChatViewModel = viewModel()) {
    Scaffold() { innerPadding ->
        println(innerPadding)

    }
}
