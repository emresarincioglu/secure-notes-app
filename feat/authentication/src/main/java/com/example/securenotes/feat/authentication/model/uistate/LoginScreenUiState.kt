package com.example.securenotes.feat.authentication.model.uistate

data class LoginScreenUiState(
    var passwordInput: String = "",
    val userPassword: String? = "",
    val loginAttemptCount: Int = 0,
    val biometricLoginEnabled: Boolean = false,
    val screenLockLoginEnabled: Boolean = false
)
