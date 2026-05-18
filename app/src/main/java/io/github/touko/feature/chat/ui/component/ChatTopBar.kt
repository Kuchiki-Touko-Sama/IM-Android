package io.github.touko.feature.chat.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.touko.navigation.NavigatorManager


@Composable
fun ChatTopBar(
    userName: String,
    onlineStatus: String,
    avatarUrl: String? = null
) {
    TopAppBar(
        navigationIcon = {
            IconButton({ NavigatorManager.back() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        userName,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        onlineStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(48.dp))
        },
        modifier = Modifier.clip(
            RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ),
        // 设置顶栏颜色和高度
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}
