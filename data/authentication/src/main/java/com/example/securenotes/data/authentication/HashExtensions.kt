package com.example.securenotes.data.authentication

import android.util.Base64
import java.security.MessageDigest

fun String.toSha256(): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(encodeToByteArray())
    return Base64.encodeToString(digest, Base64.DEFAULT)
}
