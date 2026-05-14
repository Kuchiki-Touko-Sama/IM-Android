package io.github.touko.feature.home.ui.componement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.github.touko.data.local.LocalUserManager
import io.github.touko.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting(onNavigateToAbout: () -> Unit) {
    val userName by remember { mutableStateOf(LocalUserManager.getUsername() ?: "") }
    val avatarUrl by remember { mutableStateOf(LocalUserManager.getAvatarUrl()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    var editedName by remember { mutableStateOf(userName) }
    var editedAvatar by remember { mutableStateOf(avatarUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        SettingsItem(
            title = "个人信息",
            subtitle = "查看和编辑您的个人信息",
            icon = Icons.Default.Person,
            onClick = { showEditDialog = true }
        )

        HorizontalDivider(
            Modifier,
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        SettingsItem(
            title = "主题设置",
            subtitle = "切换明暗主题和壁纸颜色",
            icon = Icons.Default.Palette,
            onClick = { }
        )

        SettingsItem(
            title = "明暗模式",
            subtitle = if (ThemeManager.darkMode) "当前：深色模式" else "当前：浅色模式",
            icon = if (ThemeManager.darkMode) Icons.Default.Nightlight else Icons.Default.LightMode,
            onClick = {
                ThemeManager.changeDarkMode(!ThemeManager.darkMode)
            }
        )

        SettingsItem(
            title = "壁纸动态取色",
            subtitle = if (ThemeManager.dynamicColor) "当前：已开启" else "当前：已关闭",
            icon = Icons.Default.ColorLens,
            onClick = {
                ThemeManager.changeDynamicColor(!ThemeManager.dynamicColor)
            }
        )

        HorizontalDivider(
            Modifier,
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        SettingsItem(
            title = "关于",
            subtitle = "查看应用信息",
            icon = Icons.Default.Info,
            onClick = { showAboutDialog = true }
        )
    }

    if (showEditDialog) {
        EditUserDialog(
            userName = editedName,
            avatarUrl = editedAvatar,
            onDismiss = { showEditDialog = false },
            onSave = { name, avatar ->
                editedName = name
                editedAvatar = avatar
                LocalUserManager.changeCurrentUser(
                    LocalUserManager.getUid(),
                    name,
                    avatar
                )
                showEditDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    userName: String,
    avatarUrl: String?,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(userName) }
    var avatar by remember { mutableStateOf(avatarUrl ?: "") }
    var showImagePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "编辑个人信息",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        AsyncImage(
                            model = avatar.ifBlank { null },
                            contentDescription = "头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            placeholder = androidx.compose.ui.graphics.painter.ColorPainter(
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                    Button(
                        onClick = { showImagePicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("更换头像")
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(name, avatar.ifBlank { null })
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )

    if (showImagePicker) {
        AlertDialog(
            onDismissRequest = { showImagePicker = false },
            title = { Text("选择头像") },
            text = {
                Text("请输入头像URL（暂不支持本地选择）")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        avatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=$name"
                        showImagePicker = false
                    }
                ) {
                    Text("使用随机头像")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImagePicker = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "IM Android App",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "版本 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                Text(
                    text = "开发者",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "touko",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                Text(
                    text = "GitHub",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = {
                       // TODO :
                    }
                ) {
                    Text(
                        text = "github.com/touko",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}