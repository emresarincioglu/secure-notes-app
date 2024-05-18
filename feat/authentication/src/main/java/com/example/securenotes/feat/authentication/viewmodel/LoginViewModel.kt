package com.example.securenotes.feat.authentication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securenotes.feat.authentication.model.uistate.LoginScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    companion object {
        private const val MAX_LOGIN_ATTEMPT = 4
    }

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getPassword()
        getLoginAttemptCount()
        getLoginSettings()
    }

    private fun getLoginAttemptCount() {
        // TODO: Get login attempt count from repository
    }

    fun isExceededLoginAttemptLimit() = uiState.value.loginAttemptCount > MAX_LOGIN_ATTEMPT

    fun increaseLoginAttemptCount() {
        val loginAttemptCount = uiState.value.loginAttemptCount
        _uiState.value = _uiState.value.copy(loginAttemptCount = loginAttemptCount + 1)
        // TODO: Increase attempt count from repository using worker
    }

    fun removeLoginAttemptCount() {
        // TODO: Delete login attempt count from repository using worker
    }

    private fun getPassword() {
        // TODO: Get user password from repository
        _uiState.value = _uiState.value.copy(userPassword = null)
    }

    fun isPasswordCorrect(): Boolean {
        // TODO: Hash and check is password correct
        return false
    }

    private fun getLoginSettings() {
        // TODO: Get biometric and screen lock settings from repository
    }
}