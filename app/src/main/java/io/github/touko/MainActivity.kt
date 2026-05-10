package io.github.touko


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.ui.NavDisplay
import io.github.touko.ui.theme.Im_android_appTheme
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import com.tencent.mmkv.MMKV
import io.github.touko.data.local.LocalUserManager
import io.github.touko.event.GlobalEvent
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.MainPage
import io.github.touko.navigation.RegisterPage
import io.github.touko.ui.views.home.MainScreen
import io.github.touko.ui.views.login.LoginScreen
import io.github.touko.ui.views.register.RegisterScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MMKV.initialize(this)
        window.isNavigationBarContrastEnforced = false
        val startPage = if (LocalUserManager.getUid() == 0) {
            LoginPage
        } else {
            MainPage
        }

        setContent {
            Im_android_appTheme {
                val navigator = remember { mutableStateListOf<Any>(startPage) }
                // 监听http拦截器触发的401错误
                LaunchedEffect(Unit) {
                    GlobalEvent.unauthorized.collect {
                        navigator.clear()
                        navigator.add(LoginPage)
                    }
                }
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
                            MainScreen(navigator)
                        }
                        else -> NavEntry(key) { MainScreen(navigator) }
                    }
                }
            }
        }

    }
}


