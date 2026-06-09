package com.example.gerenciadordetarefascomfirebase.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciadordetarefascomfirebase.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _user = mutableStateOf<FirebaseUser?>(repository.getCurrentUser())
    val user: State<FirebaseUser?> = _user

    private val _isAuthReady = mutableStateOf(false)
    val isAuthReady: State<Boolean> = _isAuthReady

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        _isAuthReady.value = true
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, password)
            _isLoading.value = false
            result.onSuccess {
                _user.value = it
                onSuccess()
            }.onFailure {
                _error.value = it.message ?: "Erro ao fazer login"
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.register(name, email, password)
            _isLoading.value = false
            result.onSuccess {
                _user.value = it
                onSuccess()
            }.onFailure {
                _error.value = it.message ?: "Erro ao cadastrar"
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.logout()
        _user.value = null
        onSuccess()
    }
}
