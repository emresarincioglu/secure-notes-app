package com.example.securenotes.domain.settings

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetPasswordUseCase
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(password: String?) {

        authenticationRepository.setPassword(password)
        if (password == null) {
            authenticationRepository.endSession()
        } else {
            val sessionTimeout = settingsRepository.sessionTimeoutStream.first()
            authenticationRepository.restartSession(sessionTimeout)
        }
    }
}
