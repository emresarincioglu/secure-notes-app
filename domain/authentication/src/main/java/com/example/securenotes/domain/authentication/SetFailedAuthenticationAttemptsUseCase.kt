package com.example.securenotes.domain.authentication

import androidx.annotation.IntRange
import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import javax.inject.Inject

class SetFailedAuthenticationAttemptsUseCase
@Inject
constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(@IntRange(from = 0) count: Int) =
        authenticationRepository.setFailedAuthenticationAttempts(count)
}
