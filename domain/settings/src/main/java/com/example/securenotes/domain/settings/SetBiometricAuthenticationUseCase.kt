package com.example.securenotes.domain.settings

import com.example.securenotes.data.settings.SettingsRepository
import javax.inject.Inject

class SetBiometricAuthenticationUseCase
@Inject
constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(isEnabled: Boolean) =
        settingsRepository.setBiometricAuthentication(isEnabled)
}
