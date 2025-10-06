// com/example/angiday/viewmodel/LoginViewModel.kt
package com.example.angiday.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.angiday.repository.UserRepository
import com.example.angiday.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = UserRepository.get(app)
    private val session = SessionManager(app)

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    fun login(email: String, password: String, hashPassword: Boolean = false) {
        _ui.value = LoginUiState(loading = true)
        viewModelScope.launch {
            val result = repo.login(email, password, hashPassword)
            _ui.value = result.fold(
                onSuccess = {
                    session.saveUser(it)
                    LoginUiState(success = true)
                },
                onFailure = {
                    LoginUiState(error = it.message ?: "Đăng nhập thất bại")
                }
            )
        }
    }
}
