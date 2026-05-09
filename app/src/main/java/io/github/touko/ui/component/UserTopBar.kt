package io.github.touko.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopBar(
    userName: String,
    onlineStatus: String,
    avatarUrl: String? = null // 实际项目中建议传入 URL
) {
    TopAppBar(
        title = {
            // 使用 Row 水平排列头像和文字信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                // 1. 头像部分
                Box(contentAlignment = Alignment.BottomEnd) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    // 2. 在线状态圆点
                    Surface(
                        shape = CircleShape,
                        color = Color.Green, // 在线颜色
                        modifier = Modifier
                            .size(12.dp)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    ) {}
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = onlineStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        modifier = Modifier.clip(
            RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ),
        actions = {
            // 右侧可以放搜索或设置图标
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            }
        },
        // 设置顶栏颜色和高度
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer)
        )
}