package com.example.securenotes.domain.authentication

import com.example.securenotes.core.common.withResult
import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.home.repository.NoteRepository
import com.example.securenotes.data.settings.SettingsRepository
import javax.inject.Inject

class DeleteDataUseCase
@Inject
constructor(
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() = withResult {
        authenticationRepository.endSession()
        authenticationRepository.deleteAuthenticationSettings()
        settingsRepository.deleteSettings()
        noteRepository.deleteData()
        true
    }
}
