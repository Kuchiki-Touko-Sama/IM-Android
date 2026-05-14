package io.github.touko.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.touko.data.local.LocalUserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val uid: Int = 0,
    val username: String = "",
    val avatarUrl: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = ProfileState(
                uid = LocalUserManager.getUid(),
                username = LocalUserManager.getUsername() ?: ""
            )
        }
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch {
            LocalUserManager.changeCurrentUser(LocalUserManager.getUid(), newName)
            _state.value = _state.value.copy(username = newName)
        }
    }
}
