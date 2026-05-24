package io.github.touko

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.remote.AppLifecycleObserver
import io.github.touko.feature.chat.ui.ChatScreen
import io.github.touko.feature.home.ui.MainScreen
import io.github.touko.feature.login.ui.LoginScreen
import io.github.touko.feature.profile.ui.ProfileScreen
import io.github.touko.feature.register.ui.RegisterScreen
import io.github.touko.navigation.ChatPage
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.MainPage
import io.github.touko.navigation.NavigatorManager
import io.github.touko.navigation.ProfilePage
import io.github.touko.navigation.RegisterPage
import io.github.touko.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        val startPage = if (LocalUserManager.getUid() == 0) LoginPage else MainPage
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(AppLifecycleObserver())

        setContent {
            AppTheme {
                val context = LocalContext.current
                var lastBackTime by remember { mutableLongStateOf(0L) }
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                if (NavigatorManager.backStack.isEmpty()) {
                    NavigatorManager.replace(startPage)
                }
                // 2.  BackHandler，强制拦截所有返回事件
                Scaffold(
                    snackbarHost = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                    shape = CircleShape,
                                    modifier = Modifier.widthIn(max = 200.dp),
                                    containerColor = SnackbarDefaults.color.copy(alpha = 0.6f)
                                ) {
                                    Box(
                                        Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(data.visuals.message)
                                    }
                                }
                            }
                        }
                    }
                ) { _ ->
                    BackHandler(enabled = NavigatorManager.backStack.isNotEmpty()) {
                        if (NavigatorManager.backStack.size > 1) {
                            NavigatorManager.back()
                        } else {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastBackTime < 1000) {
                                (context as? Activity)?.finish()
                            } else {
                                lastBackTime = currentTime
                                scope.launch {
                                    launch {
                                        snackbarHostState.showSnackbar(
                                            message = "再按一次退出",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                    delay(1000)
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            }
                        }
                    }
                    NavDisplay(
                        backStack = NavigatorManager.backStack,
                        onBack = { NavigatorManager.back() },
                        transitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(500)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(500)
                            )
                        },
                        popTransitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(500)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(500)
                            )
                        },
                        predictivePopTransitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(500)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(500)
                            )
                        }
                    ) { key ->
                        when (key) {
                            is LoginPage -> NavEntry(key) { LoginScreen() }
                            is RegisterPage -> NavEntry(key) { RegisterScreen() }
                            is MainPage -> NavEntry(key) { MainScreen() }
                            is ChatPage -> NavEntry(key) {
                                ChatScreen(
                                    friendId = key.userId,
                                    key.userName
                                )
                            }
                            is ProfilePage -> NavEntry(key) { ProfileScreen() }
                        }
                    }
                }
            }
        }
    }
}


