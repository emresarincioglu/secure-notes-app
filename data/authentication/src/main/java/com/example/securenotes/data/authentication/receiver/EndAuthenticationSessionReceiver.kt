package com.example.securenotes.data.authentication.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.datastore.core.IOException
import com.example.securenotes.core.common.di.ApplicationScope
import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EndAuthenticationSessionReceiver : BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 0
    }

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var authenticationRepository: AuthenticationRepository

    override fun onReceive(context: Context, intent: Intent) {

        val pendingResult = goAsync()
        applicationScope.launch {
            try {
                authenticationRepository.endSession()
            } catch (ioException: IOException) {
                val rescheduleCount = intent.getIntExtra("reschedule_count", 0)
                val rescheduleLimit = intent.getIntExtra("reschedule_limit", 8)
                if (rescheduleCount < rescheduleLimit) {
                    val triggerTime = Calendar.getInstance().apply {
                        add(Calendar.SECOND, 2)
                    }.timeInMillis
                    reschedule(context, triggerTime, rescheduleCount + 1, rescheduleLimit)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun reschedule(context: Context, triggerMillis: Long, count: Int, limit: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context.applicationContext, EndAuthenticationSessionReceiver::class.java).apply {
                putExtra("reschedule_count", count)
                putExtra("reschedule_limit", limit)
            },
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, intent)
    }
}
