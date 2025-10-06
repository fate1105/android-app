package com.example.angiday.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.angiday.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = UserRepository.get(app)

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

    fun register(name: String, email: String, password: String, hashPassword: Boolean = false) {
        _ui.value = RegisterUiState(loading = true)
        viewModelScope.launch {
            val result = repo.registerUser(
                name = name,
                email = email,
                passwordPlain = password,
                preferences = null,
                hashPassword = hashPassword
            )
            _ui.value = result.fold(
                onSuccess = { RegisterUiState(success = true) },
                onFailure = { RegisterUiState(error = it.message ?: "Có lỗi xảy ra") }
            )
        }
    }
}