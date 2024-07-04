package com.example.securenotes.domain.authentication

import com.example.securenotes.core.common.withResult
import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import javax.inject.Inject

class LogInWithPasswordUseCase
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val startAuthenticationSessionUseCase: StartAuthenticationSessionUseCase
) {
    suspend operator fun invoke(password: String) = withResult {
        val isPasswordCorrect = authenticationRepository.isPasswordCorrect(password)
        if (isPasswordCorrect) {
            authenticationRepository.setFailedAuthenticationAttempts(0)
            startAuthenticationSessionUseCase()
        }

        isPasswordCorrect
    }
}
