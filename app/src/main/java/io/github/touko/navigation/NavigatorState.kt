package io.github.touko.navigation

import androidx.compose.runtime.mutableStateListOf

object NavigatorState {

    val backStack = mutableStateListOf<Page>()

    fun navigate(page: Page) {
        backStack.add(page)
    }

    fun replace(page: Page) {
        backStack.clear()
        backStack.add(page)
    }

    fun back() {
        backStack.removeLastOrNull()
    }
}