package com.example.securenotes.data.authentication.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import com.example.securenotes.core.common.di.ApplicationScope
import com.example.securenotes.core.common.di.DefaultDispatcher
import com.example.securenotes.data.authentication.local.AuthenticationDataSource
import com.example.securenotes.data.authentication.local.AuthenticationDataSource.Companion.AUTH_SESSION_END_TIME_KEY
import com.example.securenotes.data.authentication.local.AuthenticationDataSource.Companion.FAILED_AUTH_ATTEMPTS_KEY
import com.example.securenotes.data.authentication.local.AuthenticationDataSource.Companion.PASSWORD_HASH_KEY
import com.example.securenotes.data.authentication.receiver.EndAuthenticationSessionReceiver
import com.example.securenotes.data.authentication.toSha256
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class DefaultAuthenticationRepository
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val localDataSource: AuthenticationDataSource,
    @ApplicationScope private val externalScope: CoroutineScope,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : AuthenticationRepository {

    private val _isAuthenticatedStream = MutableSharedFlow<Boolean>(replay = 1)
    override val isAuthenticatedStream = _isAuthenticatedStream.asSharedFlow()

    override val isPasswordCreatedStream = localDataSource.isPasswordCreatedStream

    override val failedAuthenticationAttemptsStream =
        localDataSource.failedAuthenticationAttemptsStream

    init {
        externalScope.launch {
            val isSessionOver =
                localDataSource.getPreference(AUTH_SESSION_END_TIME_KEY)?.let { endTime ->
                    System.currentTimeMillis() >= endTime
                } ?: true
            _isAuthenticatedStream.emit(!isSessionOver)
        }
    }

    /**
     * @param duration Authentication session duration as minutes.
     */
    override suspend fun startSession(duration: Int) {
        setSessionEndTime(
            Calendar.getInstance()
                .apply { add(Calendar.MINUTE, duration) }
                .timeInMillis
        )
        _isAuthenticatedStream.emit(true)
    }

    /**
     * @param duration Authentication session duration as minutes.
     */
    override suspend fun restartSession(duration: Int) = startSession(duration)

    override suspend fun endSession() {
        setSessionEndTime(null)
        _isAuthenticatedStream.emit(false)
    }

    override suspend fun isPasswordCorrect(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }

        return localDataSource.getPreference(PASSWORD_HASH_KEY)?.let { storedHash ->
            withContext(defaultDispatcher) {
                password.toSha256() == storedHash
            }
        } ?: false
    }

    /**
     * Hashes and writes password to datastore.
     * If password is null or blank, deletes entry from datastore.
     */
    override suspend fun setPassword(password: String?) {

        if (password == null) {
            localDataSource.removePreference(PASSWORD_HASH_KEY)
        } else {
            withContext(defaultDispatcher) {
                localDataSource.setPreference(PASSWORD_HASH_KEY, password.toSha256())
            }
        }
    }

    override suspend fun setFailedAuthenticationAttempts(@IntRange(from = 0) count: Int) =
        localDataSource.setPreference(FAILED_AUTH_ATTEMPTS_KEY, count)

    override suspend fun deleteAuthenticationSettings() = localDataSource.deleteSettings()

    /**
     * @param endTime Authentication session end time as epoch millis. If null, deletes entry.
     */
    private suspend fun setSessionEndTime(endTime: Long?) {

        if (endTime == null) {
            localDataSource.removePreference(AUTH_SESSION_END_TIME_KEY)
            return
        }

        localDataSource.setPreference(AUTH_SESSION_END_TIME_KEY, endTime)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = PendingIntent.getBroadcast(
            context,
            EndAuthenticationSessionReceiver.REQUEST_CODE,
            Intent(context.applicationContext, EndAuthenticationSessionReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTime, intent)
    }
}
