package com.example.securenotes.feat.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.common.Result
import com.example.securenotes.domain.authentication.GetAuthenticationDataUseCase
import com.example.securenotes.domain.authentication.GetIsAuthenticatedStreamUseCase
import com.example.securenotes.domain.authentication.LogInWithPasswordUseCase
import com.example.securenotes.domain.authentication.SetFailedAuthenticationAttemptsUseCase
import com.example.securenotes.domain.authentication.StartAuthenticationSessionUseCase
import com.example.securenotes.feat.authentication.model.uistate.LoginScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val logInWithPasswordUseCase: LogInWithPasswordUseCase,
    private val getAuthenticationDataUseCase: GetAuthenticationDataUseCase,
    private val startAuthenticationSessionUseCase: StartAuthenticationSessionUseCase,
    private val setFailedAuthenticationAttemptsUseCase: SetFailedAuthenticationAttemptsUseCase,
    getIsAuthenticatedStreamUseCase: GetIsAuthenticatedStreamUseCase
) : ViewModel() {

    companion object {
        private const val FLOW_SUBSCRIPTION_TIMEOUT = 500L
    }

    private var authenticationAttemptLimit: Int? = null
    private var failedAuthenticationAttempts by Delegates.notNull<Int>()

    val remainingAuthenticationAttempts get() = authenticationAttemptLimit?.minus(failedAuthenticationAttempts)

    private val _loginResultStream = MutableSharedFlow<Result<Boolean>>()
    val loginResultStream = _loginResultStream.asSharedFlow()

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState = _uiState.combine(getIsAuthenticatedStreamUseCase()) { uiState, isAuthenticated ->
        uiState.copy(isAuthenticated = isAuthenticated)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(FLOW_SUBSCRIPTION_TIMEOUT),
        _uiState.value
    )

    init {
        getAuthenticationData()
    }

    fun increaseFailedAuthenticationAttempts() {
        failedAuthenticationAttempts++
        viewModelScope.launch {
            setFailedAuthenticationAttemptsUseCase(failedAuthenticationAttempts)
        }
    }

    fun logInWithPassword() {
        viewModelScope.launch {
            _loginResultStream.emit(Result.Loading)
            _loginResultStream.emit(logInWithPasswordUseCase(uiState.value.passwordInput))
        }
    }

    fun logInWithBiometric() {
        viewModelScope.launch {
            setFailedAuthenticationAttemptsUseCase(0)
            startAuthenticationSessionUseCase()
        }
    }

    fun deleteAllData() {
        // TODO Delete all notes and settings
    }

    private fun getAuthenticationData() {
        viewModelScope.launch {
            with(getAuthenticationDataUseCase()) {
                authenticationAttemptLimit = authAttemptLimit
                failedAuthenticationAttempts = failedAuthAttempts
                _uiState.value = uiState.value.copy(
                    isBiometricLoginEnabled = isBiometricLoginEnabled,
                    isScreenLockLoginEnabled = isScreenLockLoginEnabled,
                )
            }
        }
    }
}
