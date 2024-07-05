package com.example.securenotes.domain.authentication.model

data class AuthenticationData(
    val authAttemptLimit: Int?,
    val failedAuthAttempts: Int,
    val isBiometricLoginEnabled: Boolean,
    val isScreenLockLoginEnabled: Boolean
)
