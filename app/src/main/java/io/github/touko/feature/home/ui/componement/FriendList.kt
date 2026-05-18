package io.github.touko.feature.home.ui.componement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.touko.data.model.response.Friendship
import io.github.touko.data.model.response.LastMessage
import io.github.touko.navigation.ChatPage
import io.github.touko.navigation.NavigatorManager


// 1. 定义一个稳定的 UI 状态包装类
@Immutable
data class FriendListState(
    val friends: List<Friendship> = emptyList(),
    val lastMessages: Map<Int, LastMessage> = emptyMap()
)

@Composable
fun FriendList(
    modifier: Modifier = Modifier,
    uiState: FriendListState
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = modifier
    ) {
        items(items = uiState.friends, key = { it.friendshipId }) { friend ->
            val lastMessage = uiState.lastMessages[friend.friendId]
            val messageContent = lastMessage?.content ?: "暂无消息"
            ListItem(
                headlineContent = {
                    Text(
                        friend.friendName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                },
                supportingContent = {
                    Text(
                        messageContent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (messageContent == "暂无消息")
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(true) {
                        NavigatorManager.goTo(
                            ChatPage(friend.friendId, friend.friendName)
                        )
                    }
            )
        }
    }
}
