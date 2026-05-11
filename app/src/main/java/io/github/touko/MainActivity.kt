package io.github.touko


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.tencent.mmkv.MMKV
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.remote.ChatWebSocketManager
import io.github.touko.navigation.ChatPage
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.MainPage
import io.github.touko.navigation.NavigatorManager
import io.github.touko.navigation.RegisterPage
import io.github.touko.ui.theme.Im_android_appTheme
import io.github.touko.feature.chat.ui.ChatScreen
import io.github.touko.feature.home.ui.MainScreen
import io.github.touko.feature.login.ui.LoginScreen
import io.github.touko.feature.register.ui.RegisterScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MMKV.initialize(this)
        window.isNavigationBarContrastEnforced = false
        val startPage = if (LocalUserManager.getUid() == 0) LoginPage else MainPage

        val userId = LocalUserManager.getUid()
        if (userId != 0)
            ChatWebSocketManager.connect(userId)
        setContent {
            Im_android_appTheme {
                if (NavigatorManager.backStack.isEmpty()) {
                    NavigatorManager.replace(startPage)
                }
                NavDisplay(
                    backStack = NavigatorManager.backStack,
                    onBack = { NavigatorManager.back() },
                ) { key ->
                    when (key) {
                        is LoginPage -> NavEntry(key) { LoginScreen() }
                        is RegisterPage -> NavEntry(key) { RegisterScreen() }
                        is MainPage -> NavEntry(key) { MainScreen() }
                        is ChatPage -> NavEntry(key) {
                            ChatScreen(
                                userId = key.userId,
                                key.userName
                            )
                        }
                    }
                }
            }
        }
    }
}


