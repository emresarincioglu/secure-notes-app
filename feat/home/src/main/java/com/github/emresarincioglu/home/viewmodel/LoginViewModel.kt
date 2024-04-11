package com.github.emresarincioglu.home.viewmodel

import androidx.lifecycle.ViewModel
import com.github.emresarincioglu.home.model.uistate.LoginScreenUiState
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
        getBiometricSetting()
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

    private fun getBiometricSetting() {
        // TODO: Get biometric feature setting from repository
        _uiState.value = _uiState.value.copy(biometricsEnabled = true)
    }
}