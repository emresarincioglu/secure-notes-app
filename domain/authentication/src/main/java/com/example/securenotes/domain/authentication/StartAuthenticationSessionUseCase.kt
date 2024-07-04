package com.example.securenotes.domain.authentication

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StartAuthenticationSessionUseCase
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        val sessionTimeout = settingsRepository.sessionTimeoutStream.first()
        authenticationRepository.startSession(sessionTimeout)
    }
}
