package io.github.touko.feature.home.ui.componement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
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
import io.github.touko.data.state.CurrentUserState
import io.github.touko.data.state.FriendState

@Composable
fun PendingApplyList(
    applyList: List<FriendshipApply>,
    onAccept: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
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
                       IconButton(
                           onClick = {
                               onAccept(apply.friendApplyId, apply.senderId)
                           },
                           colors = IconButtonDefaults.iconButtonColors().copy(
                               containerColor = MaterialTheme.colorScheme.primaryContainer,
                               contentColor = MaterialTheme.colorScheme.primary
                           ),
                       ) {
                           Icon(
                               imageVector = Icons.Default.Check,

                               contentDescription = null
                           )
                       }
                   },
                   modifier = Modifier.fillMaxWidth()
               )
           }
           }
    }
}