package com.example.securenotes.data.authentication

import android.util.Base64
import java.security.MessageDigest

object CryptoManager {
    fun computeSha256(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(data.toByteArray())
        return Base64.encodeToString(digest, Base64.DEFAULT)
    }
}
