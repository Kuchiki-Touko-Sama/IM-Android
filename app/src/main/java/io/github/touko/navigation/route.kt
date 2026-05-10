package io.github.touko.navigation

sealed class Page
data object LoginPage: Page()
data object RegisterPage: Page()
data object MainPage: Page()

data class ChatPage(val userId: Int, val userName: String) : Page()