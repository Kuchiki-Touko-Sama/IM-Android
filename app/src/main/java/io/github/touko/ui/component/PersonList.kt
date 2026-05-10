package io.github.touko.ui.component

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.touko.data.model.response.TargetUser
import io.github.touko.data.state.CurrentUserState
import io.github.touko.data.state.FriendState
import io.github.touko.data.state.FriendState.*


@Composable
fun PersonList(
    people: List<TargetUser>,
    onAddPerson: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = modifier
    ) {
        items(items = people, key = { it.userId }) { person ->
            ListItem(
                headlineContent = { Text(person.username) },
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
                            onAddPerson(person.userId)
                        },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Icon(
                            imageVector = when(CurrentUserState.friendships[person.userId]) {
                                PENDING -> Icons.Default.Check
                                FRIEND -> Icons.Default.Person2
                                NONE, null -> Icons.Default.Add
                            },
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
