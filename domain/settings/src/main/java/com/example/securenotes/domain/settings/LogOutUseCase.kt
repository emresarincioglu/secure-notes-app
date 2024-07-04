package com.example.securenotes.domain.settings

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import javax.inject.Inject

class LogOutUseCase
@Inject
constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke() = authenticationRepository.endSession()
}
