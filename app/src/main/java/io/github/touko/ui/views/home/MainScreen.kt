package io.github.touko.ui.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.touko.ui.component.FriendList
import io.github.touko.ui.component.ToolBar
import io.github.touko.ui.component.UserTopBar


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            UserTopBar("Touko", "online", null)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            FriendList(
                modifier = Modifier.fillMaxSize(),
            )
            ToolBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )
        }
    }
}
