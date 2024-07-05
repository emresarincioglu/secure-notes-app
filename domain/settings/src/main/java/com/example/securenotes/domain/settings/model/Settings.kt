package com.example.securenotes.domain.settings.model

data class Settings(
    val authAttemptLimit: Int?,
    val authSessionTimeout: Int,
    val isBiometricAuthEnabled: Boolean,
    val isScreenLockAuthEnabled: Boolean,
)
