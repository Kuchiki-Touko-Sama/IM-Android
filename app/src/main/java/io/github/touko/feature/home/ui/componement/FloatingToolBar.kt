package io.github.touko.feature.home.ui.componement


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.touko.feature.home.ui.CurrentMainTab

@Composable
private fun tabTint(
    selected: Boolean
) =
    if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToolBar(
    currentTab: CurrentMainTab,
    onTabSelected: (CurrentMainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = true,
        expandedShadowElevation = 9.dp,
        modifier = modifier.padding(bottom = 10.dp),
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
                onClick = { onTabSelected(CurrentMainTab.ChatList) },
                modifier = Modifier.width(60.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = tabTint(currentTab == CurrentMainTab.ChatList),
                )
            }
            IconButton(
                onClick = { onTabSelected(CurrentMainTab.FriendManager) },
                modifier = Modifier.width(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = tabTint(currentTab == CurrentMainTab.FriendManager),
                )
            }
            IconButton(
                onClick = { onTabSelected(CurrentMainTab.Settings) },
                modifier = Modifier.width(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = tabTint(currentTab == CurrentMainTab.Settings),
                )
            }
        }
    }
}



