package com.example.securenotes.domain.settings

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.settings.SettingsRepository
import javax.inject.Inject

class SetSessionTimeoutUseCase
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(timeout: Int) {
        settingsRepository.setSessionTimeout(timeout)
        authenticationRepository.restartSession(timeout)
    }
}
