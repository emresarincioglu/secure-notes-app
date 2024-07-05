package com.example.securenotes.feat.authentication.model.uistate

data class LoginScreenUiState(
    var passwordInput: String = "",
    val isAuthenticated: Boolean = false,
    val isBiometricLoginEnabled: Boolean = false,
    val isScreenLockLoginEnabled: Boolean = false
)
