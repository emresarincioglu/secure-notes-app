package com.github.emresarincioglu.home.model.uistate

data class LoginScreenUiState(
    var passwordInput: String = "",
    val userPassword: String? = "",
    val loginAttemptCount: Int = 0,
    val biometricsEnabled: Boolean = false
)
