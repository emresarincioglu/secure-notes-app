package com.example.securenotes.data.authentication.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class SettingsDataSource @Inject constructor(
    @Named("settingsDataStore") private val dataStore: DataStore<Preferences>
) {

    companion object {
        val IS_BIOMETRIC_AUTH_ENABLED_KEY =
            booleanPreferencesKey("is_biometric_auth_enabled")
        val IS_SCREEN_LOCK_AUTH_ENABLED_KEY =
            booleanPreferencesKey("is_screen_lock_auth_enabled")
        val AUTH_ATTEMPT_LIMIT_KEY = intPreferencesKey("auth_attempt_limit")
        val AUTH_SESSION_TIMEOUT_KEY = intPreferencesKey("auth_session_timeout")

        private const val DEFAULT_BIOMETRIC_AUTH_STATUS = false
        private const val DEFAULT_SCREEN_LOCK_AUTH_STATUS = false
        private const val DEFAULT_AUTH_ATTEMPT_LIMIT = 10
        private const val DEFAULT_AUTH_SESSION_TIMEOUT = 3
    }

    val isBiometricAuthenticationEnabledStream = dataStore.data.map { preferences ->
        preferences[IS_BIOMETRIC_AUTH_ENABLED_KEY] ?: DEFAULT_BIOMETRIC_AUTH_STATUS
    }

    val isScreenLockAuthenticationEnabledStream = dataStore.data.map { preferences ->
        preferences[IS_SCREEN_LOCK_AUTH_ENABLED_KEY] ?: DEFAULT_SCREEN_LOCK_AUTH_STATUS
    }

    val authenticationAttemptLimitStream = dataStore.data.map { preferences ->
        preferences[AUTH_ATTEMPT_LIMIT_KEY] ?: DEFAULT_AUTH_ATTEMPT_LIMIT
    }

    val sessionTimeoutStream = dataStore.data.map { preferences ->
        preferences[AUTH_SESSION_TIMEOUT_KEY] ?: DEFAULT_AUTH_SESSION_TIMEOUT
    }

    suspend fun <T> getPreference(key: Preferences.Key<T>): T? {
        return dataStore.data.first()[key]
    }

    suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
