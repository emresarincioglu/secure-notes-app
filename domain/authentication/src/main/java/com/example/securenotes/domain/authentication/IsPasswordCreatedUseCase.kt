package com.example.securenotes.domain.authentication

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class IsPasswordCreatedUseCase
@Inject
constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke() = authenticationRepository.isPasswordCreatedStream.first()
}
