package com.example.securenotes.domain.settings

import com.example.securenotes.data.settings.SettingsRepository
import com.example.securenotes.domain.settings.model.Settings
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSettingsUseCase
@Inject
constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke() = coroutineScope {

        val isBiometricAuthEnabled =
            async { settingsRepository.isBiometricAuthenticationEnabledStream.first() }
        val isScreenLockAuthEnabled =
            async { settingsRepository.isScreenLockAuthenticationEnabledStream.first() }
        val authAttemptLimit = async { settingsRepository.authenticationAttemptLimitStream.first() }
        val authSessionTimeout = async { settingsRepository.sessionTimeoutStream.first() }

        Settings(
            isBiometricAuthEnabled = isBiometricAuthEnabled.await(),
            isScreenLockAuthEnabled = isScreenLockAuthEnabled.await(),
            authAttemptLimit = authAttemptLimit.await(),
            authSessionTimeout = authSessionTimeout.await()
        )
    }
}
