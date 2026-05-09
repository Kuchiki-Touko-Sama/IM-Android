package io.github.touko.ui.component

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.selects.select

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToolBar(modifier: Modifier = Modifier) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier.width(300.dp),
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {},
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
        }

        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}



