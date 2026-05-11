package io.github.touko.navigation

import androidx.compose.runtime.mutableStateListOf

object NavigatorManager {

    val backStack = mutableStateListOf<Page>()

    fun goTo(page: Page) {
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