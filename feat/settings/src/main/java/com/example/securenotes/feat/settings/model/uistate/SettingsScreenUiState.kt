package com.example.securenotes.feat.settings.model.uistate

import androidx.annotation.IntRange

data class SettingsScreenUiState(
    val isAuthenticated: Boolean = false,
    val isPasswordCreated: Boolean = false,
    var isBiometricAuthEnabled: Boolean = false,
    var isScreenLockAuthEnabled: Boolean = false,
    @IntRange(from = 1) val sessionTimeout: Int = 1,
    @IntRange(from = 1) val authenticationAttemptLimit: Int? = null
)
