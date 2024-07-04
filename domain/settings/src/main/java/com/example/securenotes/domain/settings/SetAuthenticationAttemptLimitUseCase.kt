package com.example.securenotes.domain.settings

import com.example.securenotes.data.settings.SettingsRepository
import javax.inject.Inject

class SetAuthenticationAttemptLimitUseCase
@Inject
constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(limit: Int?) =
        settingsRepository.setAuthenticationAttemptLimit(limit)
}
