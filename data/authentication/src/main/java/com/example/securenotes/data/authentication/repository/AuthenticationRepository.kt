package com.example.securenotes.data.authentication.repository

import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    val isAuthenticatedStream: Flow<Boolean>

    val isPasswordCreatedStream: Flow<Boolean>

    val failedAuthenticationAttemptsStream: Flow<Int>

    suspend fun startSession(duration: Int)

    suspend fun restartSession(duration: Int)

    suspend fun endSession()

    suspend fun isPasswordCorrect(password: String): Boolean

    suspend fun setPassword(password: String?)

    suspend fun setFailedAuthenticationAttempts(count: Int)
}
