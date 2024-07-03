package com.example.securenotes.data.authentication.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class AuthenticationDataSource @Inject constructor(
    @Named("authenticationDataStore") private val dataStore: DataStore<Preferences>
) {

    companion object {
        val PASSWORD_HASH_KEY = stringPreferencesKey("user_password_hash")
        val FAILED_AUTH_ATTEMPTS_KEY = intPreferencesKey("failed_auth_attempts")
        val AUTH_SESSION_END_TIME_KEY = longPreferencesKey("auth_session_end_time")

        private const val DEFAULT_FAILED_AUTH_ATTEMPTS_COUNT = 0
    }

    val isPasswordCreatedStream = dataStore.data.map { preferences ->
        preferences[PASSWORD_HASH_KEY] != null
    }

    val failedAuthenticationAttemptsStream = dataStore.data.map { preferences ->
        preferences[FAILED_AUTH_ATTEMPTS_KEY] ?: DEFAULT_FAILED_AUTH_ATTEMPTS_COUNT
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
