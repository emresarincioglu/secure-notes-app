package com.example.securenotes.feat.settings.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import com.example.securenotes.feat.settings.model.uistate.SettingsScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getSettingsFromRepository()
    }

    private fun getSettingsFromRepository() {
        // TODO: Get settings from repository
        // TODO: Disable authentication switches if password not available
    }

    fun setPassword(password: String) {
        // TODO: Change password
        _uiState.value = _uiState.value.copy(isPasswordAvailable = true)
    }

    fun removePassword() {
        // TODO: Remove password
        _uiState.value = _uiState.value.copy(isPasswordAvailable = false)
    }

    fun setBiometricLogin(isEnabled: Boolean) {
        // TODO: Save biometric login preference
    }

    fun setScreenLockLogin(isEnabled: Boolean) {
        // TODO: Save screen lock login preference
    }

    fun setLoginAttemptLimit(@IntRange(from = 0) limit: Int) {
        _uiState.value = _uiState.value.copy(loginAttemptLimit = limit)
    }

    fun setSessionTimeout(timeout: Int) {
        // TODO: Change session timeout
        _uiState.value = _uiState.value.copy(sessionTimeout = timeout)
    }

    fun logOut() {
        // TODO: Log out
    }
}