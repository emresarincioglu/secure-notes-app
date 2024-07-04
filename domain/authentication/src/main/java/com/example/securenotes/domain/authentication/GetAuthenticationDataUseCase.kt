package com.example.securenotes.domain.authentication

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.settings.SettingsRepository
import com.example.securenotes.domain.authentication.model.AuthenticationData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAuthenticationDataUseCase
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository,
    private val isPasswordCreatedUseCase: IsPasswordCreatedUseCase,
) {
    suspend operator fun invoke() = coroutineScope {

        val isPasswordCreated = async { isPasswordCreatedUseCase() }
        val authAttemptLimit = async { settingsRepository.authenticationAttemptLimitStream.first() }
        val failedAuthAttempts = async {
            authenticationRepository.failedAuthenticationAttemptsStream.first()
        }
        val isBiometricAuthEnabled = async {
            settingsRepository.isBiometricAuthenticationEnabledStream.first()
        }
        val isScreenLockAuthEnabled = async {
            settingsRepository.isScreenLockAuthenticationEnabledStream.first()
        }

        AuthenticationData(
            authAttemptLimit = authAttemptLimit.await(),
            isPasswordCreated = isPasswordCreated.await(),
            failedAuthAttempts = failedAuthAttempts.await(),
            isBiometricLoginEnabled = isBiometricAuthEnabled.await(),
            isScreenLockLoginEnabled = isScreenLockAuthEnabled.await()
        )
    }
}
