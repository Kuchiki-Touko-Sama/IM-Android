package io.github.touko.feature.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.model.request.RegisterRequest
import io.github.touko.data.remote.HttpClient
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.NavigatorManager
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    fun updateUsername(newValue: String) {
        username = newValue
    }
    fun updatePassword(newValue: String) {
        password = newValue
    }

    fun updateConfirmPassword(newValue: String) {
        confirmPassword = newValue
    }

    fun register() {
        if (!isValidForm())
            return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = HttpClient.userApi.register(RegisterRequest(username, password))
            if (response.code == 200)
                NavigatorManager.goTo(LoginPage)
            else
                errorMessage = response.message

            isLoading = false
        }
    }


    fun isValidForm(): Boolean {
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "所有字段均为必填项"
            return false
        }
        if (password != confirmPassword) {
            errorMessage = "两次输入密码不一致"
            return false
        }
        return true
    }
}