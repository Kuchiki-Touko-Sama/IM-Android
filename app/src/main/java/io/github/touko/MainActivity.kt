package io.github.touko

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.ui.NavDisplay
import io.github.touko.ui.theme.Im_android_appTheme
import androidx.compose.runtime.remember

import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation3.runtime.NavEntry
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.MainPage
import io.github.touko.navigation.RegisterPage
import io.github.touko.ui.views.home.MainScreen
import io.github.touko.ui.views.login.LoginScreen
import io.github.touko.ui.views.register.RegisterScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.isNavigationBarContrastEnforced = false
        setContent {
            Im_android_appTheme {
                val navigator = remember { mutableStateListOf<Any>(LoginPage) }
                NavDisplay(
                    backStack = navigator,
                    onBack = { navigator.removeLastOrNull() },
                ) { key ->
                    when (key) {
                        is LoginPage -> NavEntry(key) {
                            LoginScreen(navigator)
                        }
                        is RegisterPage -> NavEntry(key) {
                            RegisterScreen(navigator)
                        }
                        is MainPage -> NavEntry(key) {
                            MainScreen()
                        }
                        else -> NavEntry(key) { MainScreen() }
                    }
                }
            }
        }
    }
}


