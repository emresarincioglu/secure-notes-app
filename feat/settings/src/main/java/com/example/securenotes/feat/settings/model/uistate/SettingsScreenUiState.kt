package com.example.securenotes.feat.settings.model.uistate

import androidx.annotation.IntRange

data class SettingsScreenUiState(
    val isPasswordAvailable: Boolean = false,
    var isBiometricEnabled: Boolean = false,
    var isScreenLockEnabled: Boolean = false,
    @IntRange(from = 0) val loginAttemptLimit: Int = 0,
    @IntRange(from = 1, to = 30) val sessionTimeout: Int = 1
)
