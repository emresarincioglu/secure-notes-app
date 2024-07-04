package com.example.securenotes.data.settings

import androidx.annotation.IntRange
import com.example.securenotes.data.settings.local.SettingsDataSource
import com.example.securenotes.data.settings.local.SettingsDataSource.Companion.AUTH_ATTEMPT_LIMIT_KEY
import com.example.securenotes.data.settings.local.SettingsDataSource.Companion.AUTH_SESSION_TIMEOUT_KEY
import com.example.securenotes.data.settings.local.SettingsDataSource.Companion.IS_BIOMETRIC_AUTH_ENABLED_KEY
import com.example.securenotes.data.settings.local.SettingsDataSource.Companion.IS_SCREEN_LOCK_AUTH_ENABLED_KEY
import javax.inject.Inject

class DefaultSettingsRepository
@Inject
constructor(private val localDataSource: SettingsDataSource) : SettingsRepository {

    override val isBiometricAuthenticationEnabledStream =
        localDataSource.isBiometricAuthenticationEnabledStream

    override val isScreenLockAuthenticationEnabledStream =
        localDataSource.isScreenLockAuthenticationEnabledStream

    override val authenticationAttemptLimitStream = localDataSource.authenticationAttemptLimitStream

    override val sessionTimeoutStream = localDataSource.sessionTimeoutStream

    override suspend fun setBiometricAuthentication(isEnabled: Boolean) =
        localDataSource.setPreference(IS_BIOMETRIC_AUTH_ENABLED_KEY, isEnabled)

    override suspend fun setScreenLockAuthentication(isEnabled: Boolean) =
        localDataSource.setPreference(IS_SCREEN_LOCK_AUTH_ENABLED_KEY, isEnabled)

    override suspend fun setAuthenticationAttemptLimit(@IntRange(from = 1) limit: Int?) {
        if (limit == null) {
            localDataSource.removePreference(AUTH_ATTEMPT_LIMIT_KEY)
        } else {
            localDataSource.setPreference(AUTH_ATTEMPT_LIMIT_KEY, limit)
        }
    }

    /**
     * @param timeout Authentication session timeout as minutes.
     */
    override suspend fun setSessionTimeout(@IntRange(from = 1) timeout: Int) =
        localDataSource.setPreference(AUTH_SESSION_TIMEOUT_KEY, timeout)
}
