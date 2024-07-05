package com.example.securenotes.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securenotes.domain.authentication.GetIsAuthenticatedStreamUseCase
import com.example.securenotes.domain.authentication.IsPasswordCreatedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isPasswordCreatedUseCase: IsPasswordCreatedUseCase,
    private val getIsAuthenticatedStreamUseCase: GetIsAuthenticatedStreamUseCase
) : ViewModel() {

    var isAuthenticated: Boolean? = null
        private set

    var isPasswordCreated: Boolean? = null
        private set

    suspend fun getData() {
        coroutineScope {
            val isAuthenticatedJob = async { getIsAuthenticatedStreamUseCase().first() }
            val isPasswordCreatedJob = async { isPasswordCreatedUseCase() }

            isAuthenticated = isAuthenticatedJob.await()
            isPasswordCreated = isPasswordCreatedJob.await()
        }
    }
}