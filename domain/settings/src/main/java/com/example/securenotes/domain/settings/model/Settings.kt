package com.example.securenotes.domain.settings.model

import androidx.annotation.IntRange

data class Settings(
    val isBiometricAuthEnabled: Boolean,
    val isScreenLockAuthEnabled: Boolean,
    @IntRange(from = 0) val authAttemptLimit: Int?,
    @IntRange(from = 1) val authSessionTimeout: Int
)
