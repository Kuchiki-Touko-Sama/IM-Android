package io.github.touko.feature.home.ui.componement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.touko.data.model.response.FriendshipApply
import io.github.touko.feature.home.state.CurrentUserState
import io.github.touko.feature.home.state.FriendState

@Composable
fun PendingApplyList(
    modifier: Modifier = Modifier,
    applyList: List<FriendshipApply>,
    onAccept: (Int, Int) -> Unit,
    onRefuse: (Int, Int) -> Unit

) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = modifier
    ) {
        items(items = applyList, key = { it.friendApplyId }) { apply ->
            if (CurrentUserState.friendships[apply.senderId] != FriendState.FRIEND) {
                ListItem(
                    headlineContent = { Text(apply.userName) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = {
                                    onAccept(apply.friendApplyId, apply.senderId)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                            ) { Icon(Icons.Default.Check, null) }

                            IconButton(
                                onClick = {
                                    onRefuse(apply.friendApplyId, apply.senderId)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) { Icon(Icons.Default.Close, null) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}