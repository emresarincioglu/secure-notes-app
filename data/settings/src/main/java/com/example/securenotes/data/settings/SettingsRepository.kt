package com.example.securenotes.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isBiometricAuthenticationEnabledStream: Flow<Boolean>

    val isScreenLockAuthenticationEnabledStream: Flow<Boolean>

    val authenticationAttemptLimitStream: Flow<Int>

    val sessionTimeoutStream: Flow<Int>

    suspend fun setBiometricAuthentication(isEnabled: Boolean)

    suspend fun setScreenLockAuthentication(isEnabled: Boolean)

    suspend fun setAuthenticationAttemptLimit(limit: Int?)

    suspend fun setSessionTimeout(timeout: Int)
}
