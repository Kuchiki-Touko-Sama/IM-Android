package io.github.touko.feature.home.ui.componement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.touko.data.local.LocalUserManager
import io.github.touko.navigation.NavigatorManager
import io.github.touko.navigation.LoginPage

@Composable
fun DropdownMenu( modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            offset = DpOffset(
                x = (-8).dp,
                y = 20.dp
            )
        ) {
            DropdownMenuItem(
                text = { Text("退出") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                onClick = {
                    expanded = false
                    LocalUserManager.logout(context)
                    NavigatorManager.replace(LoginPage)
                }
            )
        }
    }
}
