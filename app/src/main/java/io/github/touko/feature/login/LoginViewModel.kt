package io.github.touko.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.local.TokenManager
import io.github.touko.data.model.request.LoginRequest
import io.github.touko.data.remote.ChatWebSocketManager
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.remote.NoNetworkException
import io.github.touko.data.remote.safeApiCall
import io.github.touko.feature.home.state.CurrentUserState
import io.github.touko.navigation.MainPage
import io.github.touko.navigation.NavigatorManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun updateUsername(newValue: String) {
        username = newValue
    }
    fun updatePassword(newValue: String) {
        password = newValue
    }

    fun login() {
        if (!isValidForm())
            return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            safeApiCall { HttpClient.userApi.login(LoginRequest(username, password)) }
                .onSuccess { resp ->
                    if (resp.isSuccess()) {
                        TokenManager.saveToken(resp.data!!.token)
                        CurrentUserState.login(resp.data.userId, username)
                        LocalUserManager.changeCurrentUser(resp.data.userId, username)
                        ChatWebSocketManager.connect()
                        username = ""
                        password = ""
                        NavigatorManager.replace(MainPage)
                    } else
                        errorMessage = resp.message
                }
                .onFailure { e ->
                    errorMessage = when (e) {
                        is NoNetworkException -> "无网络连接"
                        else -> "网络请求失败"
                    }
                }
            isLoading = false
        }
    }

    private fun isValidForm(): Boolean {
        username = username.trim()
        password = password.trim()
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "用户名或密码不能为空"
            return false
        }
        return true
    }
}