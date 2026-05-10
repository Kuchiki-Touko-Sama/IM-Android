package io.github.touko.ui.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.R
import io.github.touko.data.state.CurrentUserState
import io.github.touko.ui.component.FriendList
import io.github.touko.ui.component.PendingApplyList
import io.github.touko.ui.component.PersonList
import io.github.touko.ui.component.ToolBar
import io.github.touko.ui.component.ToukoSearchBar
import io.github.touko.ui.component.UserTopBar

enum class CurrentMainTab {
    ChatList,
    FriendManager,
    Settings
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Scaffold(
        topBar = {
            when (viewModel.currentMainTab) {
                CurrentMainTab.ChatList, CurrentMainTab.FriendManager -> UserTopBar(
                    CurrentUserState.username,
                    "online",
                    null
                )
                else -> {}
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            when (viewModel.currentMainTab) {
                CurrentMainTab.ChatList -> {
                    Column {
                        ToukoSearchBar(
                            onSearch = {
                            },
                            placeholder = stringResource(R.string.search_message_placeholder)
                        )
                        FriendList(
                            friends = viewModel.friendList,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                CurrentMainTab.FriendManager -> {
                    Column {
                        ToukoSearchBar(
                            onSearch = viewModel::searchPeopleByName,
                            placeholder = stringResource(R.string.search_people_placeholder)
                        )
                        if (viewModel.personList.isNotEmpty()) {
                            Text("搜索结果", modifier = Modifier.padding(start = 20.dp))
                            PersonList(
                                people = viewModel.personList,
                                onAddPerson = viewModel::sendFriendshipApply
                            )
                            HorizontalDivider(modifier = Modifier.padding(10.dp))
                        }
                        if (viewModel.friendApplyList.isNotEmpty()) {
                            Text("待处理的好友申请", modifier = Modifier.padding(start = 20.dp))
                            PendingApplyList(
                                applyList = viewModel.friendApplyList,
                                onAccept = viewModel::acceptFriendApply
                            )
                        }
                    }
                }
                CurrentMainTab.Settings -> {
                    // TODO: 设置页面
                }
            }
            ToolBar(
                currentTab = viewModel.currentMainTab,
                onTabSelected = { currentTab ->
                    viewModel.currentMainTab = currentTab
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )
        }
    }
}

