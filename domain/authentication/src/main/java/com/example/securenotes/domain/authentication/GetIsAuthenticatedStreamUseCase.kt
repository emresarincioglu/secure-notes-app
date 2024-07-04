package com.example.securenotes.domain.authentication

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import javax.inject.Inject

class GetIsAuthenticatedStreamUseCase
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    operator fun invoke() = authenticationRepository.isAuthenticatedStream
}
