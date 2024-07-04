package com.example.securenotes.domain.authentication.model

data class AuthenticationData(
    val authAttemptLimit: Int?,
    val failedAuthAttempts: Int,
    val isPasswordCreated: Boolean,
    val isBiometricLoginEnabled: Boolean,
    val isScreenLockLoginEnabled: Boolean
)
