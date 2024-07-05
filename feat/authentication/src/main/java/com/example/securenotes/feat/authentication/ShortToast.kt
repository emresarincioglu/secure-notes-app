package com.example.securenotes.feat.authentication

import android.content.Context
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ShortToast {

    fun showText(
        context: Context,
        externalScope: CoroutineScope,
        text: String,
        @IntRange(from = 0, to = 3500) durationMillis: Long
    ) {
        val toast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG)
        toast.show()
        externalScope.launch {
            delay(durationMillis)
            toast.cancel()
        }
    }

    fun showText(
        context: Context,
        externalScope: CoroutineScope,
        @StringRes text: Int,
        @IntRange(from = 0, to = 3500) durationMillis: Long
    ) {
        val toast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG)
        toast.show()
        externalScope.launch {
            delay(durationMillis)
            toast.cancel()
        }
    }
}