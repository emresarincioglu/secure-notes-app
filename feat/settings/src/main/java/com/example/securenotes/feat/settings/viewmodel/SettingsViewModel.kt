package com.example.securenotes.feat.settings.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.common.Result
import com.example.securenotes.domain.authentication.GetIsAuthenticatedStreamUseCase
import com.example.securenotes.domain.authentication.IsPasswordCreatedUseCase
import com.example.securenotes.domain.settings.GetSettingsUseCase
import com.example.securenotes.domain.settings.IsPasswordCorrectUseCase
import com.example.securenotes.domain.settings.LogOutUseCase
import com.example.securenotes.domain.settings.SetAuthenticationAttemptLimitUseCase
import com.example.securenotes.domain.settings.SetBiometricAuthenticationUseCase
import com.example.securenotes.domain.settings.SetPasswordUseCase
import com.example.securenotes.domain.settings.SetScreenLockAuthenticationUseCase
import com.example.securenotes.domain.settings.SetSessionTimeoutUseCase
import com.example.securenotes.feat.settings.model.uistate.SettingsScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressWarnings("LongParameterList")
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val setPasswordUseCase: SetPasswordUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val isPasswordCorrectUseCase: IsPasswordCorrectUseCase,
    private val isPasswordCreatedUseCase: IsPasswordCreatedUseCase,
    private val setSessionTimeoutUseCase: SetSessionTimeoutUseCase,
    private val setBiometricAuthenticationUseCase: SetBiometricAuthenticationUseCase,
    private val setScreenLockAuthenticationUseCase: SetScreenLockAuthenticationUseCase,
    private val setAuthenticationAttemptLimitUseCase: SetAuthenticationAttemptLimitUseCase,
    getIsAuthenticatedStreamUseCase: GetIsAuthenticatedStreamUseCase
) : ViewModel() {

    companion object {
        private const val FLOW_SUBSCRIPTION_TIMEOUT = 500L
    }

    private val _setPasswordResultStream = MutableSharedFlow<Result<Boolean>>()
    val setPasswordResultStream = _setPasswordResultStream.asSharedFlow()

    private val _uiState = MutableStateFlow(SettingsScreenUiState())
    val uiState = _uiState.combine(getIsAuthenticatedStreamUseCase()) { uiState, isAuthenticated ->
        uiState.copy(isAuthenticated = isAuthenticated)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(FLOW_SUBSCRIPTION_TIMEOUT),
        _uiState.value
    )

    init {
        getSettings()
    }

    /**
     * Sets user password.
     * @param newPassword If null, deletes password.
     */
    fun setPassword(oldPassword: String? = null, newPassword: String?) {
        viewModelScope.launch {
            _setPasswordResultStream.emit(Result.Loading)
            if (oldPassword != null && !isPasswordCorrectUseCase(oldPassword)) {
                _setPasswordResultStream.emit(Result.Success(false))
                return@launch
            }

            if (newPassword != null && !validatePassword(newPassword)) {
                _setPasswordResultStream.emit(Result.Success(false))
                return@launch
            }

            if (newPassword == null) {
                _uiState.value = uiState.value.copy(isPasswordCreated = false)
                setPasswordUseCase(null)
                _setPasswordResultStream.emit(Result.Success(true))
            } else {
                setPasswordUseCase(newPassword)
                _uiState.value = uiState.value.copy(isPasswordCreated = true)
                _setPasswordResultStream.emit(Result.Success(true))
            }
        }
    }

    fun setBiometricAuthentication(isEnabled: Boolean) {
        viewModelScope.launch {
            setBiometricAuthenticationUseCase(isEnabled)
        }
    }

    fun setScreenLockAuthentication(isEnabled: Boolean) {
        viewModelScope.launch {
            setScreenLockAuthenticationUseCase(isEnabled)
        }
    }

    fun setAuthenticationAttemptLimit(@IntRange(from = 1) limit: Int?) {
        viewModelScope.launch {
            setAuthenticationAttemptLimitUseCase(limit)
        }
        _uiState.value = uiState.value.copy(authenticationAttemptLimit = limit)
    }

    fun setSessionTimeout(timeout: Int) {
        viewModelScope.launch {
            setSessionTimeoutUseCase(timeout)
        }
        _uiState.value = uiState.value.copy(sessionTimeout = timeout)
    }

    fun logOut() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }

    private fun getSettings() {
        viewModelScope.launch {
            val isPasswordCreated = async { isPasswordCreatedUseCase() }
            val settings = async { getSettingsUseCase() }
            with(settings.await()) {
                _uiState.value = uiState.value.copy(
                    isPasswordCreated = isPasswordCreated.await(),
                    sessionTimeout = authSessionTimeout,
                    authenticationAttemptLimit = authAttemptLimit,
                    isBiometricAuthEnabled = isBiometricAuthEnabled,
                    isScreenLockAuthEnabled = isScreenLockAuthEnabled
                )
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        val regex = """^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,20}$""".toRegex()
        return regex matches password
    }
}
