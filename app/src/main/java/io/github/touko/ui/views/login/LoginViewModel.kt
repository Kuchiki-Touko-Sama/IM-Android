package io.github.touko.ui.views.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.local.TokenManager
import io.github.touko.data.model.request.LoginRequest
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.state.CurrentUserState
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

    fun login(onSuccess: () -> Unit) {
        if (!isValidForm())
            return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = HttpClient.userApi.login(LoginRequest(username, password))
            if (response.code == 200 && response.data != null) {
                TokenManager.saveToken(response.data.token)
                // 设置当前登录用户状态
                println(response.data)
                CurrentUserState.uid = response.data.userId
                CurrentUserState.username = username
                onSuccess()
            } else {
                errorMessage = response.message
            }
            isLoading = false
        }
    }

    private fun isValidForm(): Boolean {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "用户名或密码不能为空"
            return false
        }
        return true
    }
}
