package io.github.touko.feature.chat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.touko.data.model.response.Message
import io.github.touko.feature.home.state.CurrentUserState

@Composable
fun MessageList(
    messageList: List<Message>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = 100.dp,
            start = 12.dp,
            end = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messageList) { message ->
            val isMine = (message.senderId == CurrentUserState.uid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
            ) {
                Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start) {
                    val time = message.createTime.substring(11, 16)
                    val date = message.createTime.substring(0, 10)
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            top = 2.dp,
                            start = 35.dp,
                            end = 35.dp
                        )
                    )
                    // 文字 & 头像
                    Row {
                        if (!isMine)
                            Icon(Icons.Default.Person, null)
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isMine)
                                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 10.dp
                                )
                        ) {
                            Text(
                                text = message.content,
                                color = if (isMine)
                                    MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (isMine)
                            Icon(Icons.Default.Person, null)
                    }
                }
            }
        }
    }
}