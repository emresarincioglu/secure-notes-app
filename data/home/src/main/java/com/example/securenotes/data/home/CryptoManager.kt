package com.example.securenotes.data.home

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.math.min

object CryptoManager {

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    private const val IV_SIZE = 16
    private const val KEY_SIZE = 256
    private const val KEY_ALIAS = "secure_note_app"
    private const val CHUNK_SIZE = 1024

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    /**
     * @return First element is the initialization vector, second element is the encrypted and
     * encoded data.
     */
    fun encrypt(data: String): Pair<ByteArray, String> {
        val iv = createIv()
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        }

        val encryptedData = cipher.doFinal(data.encodeToByteArray())
        return iv to Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun encrypt(data: String, iv: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        }

        val encryptedData = cipher.doFinal(data.encodeToByteArray())
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    /**
     * @throws IllegalArgumentException If [updatedChunks] is negative or [fileName] is blank
     */
    fun encryptToFile(
        context: Context,
        fileName: String,
        data: ByteArray,
        updatedChunks: Int = Int.MAX_VALUE
    ) {
        if (updatedChunks < 0) {
            throw IllegalArgumentException("updatedChunks cannot be negative number")
        }

        if (fileName.isBlank()) {
            throw IllegalArgumentException("fileName cannot be blank")
        }

        if (data.isEmpty()) {
            return
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val destination = File(context.filesDir, fileName)

        val destinationExists = destination.exists()
        if (destinationExists) {
            context.backupFileIfExists(fileName)
            context.deleteFile(fileName)
        }

        try {
            var offset = 0
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                while (data.size - offset >= CHUNK_SIZE) {
                    outputStream.writeChunk(cipher, data, offset, CHUNK_SIZE)
                    offset += CHUNK_SIZE
                }

                if (!destinationExists) {
                    outputStream.writeChunk(cipher, data, offset)
                    return
                }

                val lastDataChunk = data.copyOfRange(offset, data.size)
                context.openFileBackup(fileName).use { backup ->
                    backup.skipChunks(updatedChunks)

                    if (lastDataChunk.isEmpty()) {
                        while (true) {
                            val chunkSize = backup.readBytes(4)?.toInt() ?: return
                            outputStream.write(chunkSize.toByteArray())
                            outputStream.write(
                                backup.readBytes(getEncryptedDataSize(chunkSize) + IV_SIZE)!!
                            )
                        }
                    } else {
                        var remainder = lastDataChunk
                        while (true) {
                            val chunk = backup.readChunk(cipher)

                            val pair = chunk?.split(CHUNK_SIZE - remainder.size)
                            val complementary = pair?.first ?: ByteArray(0)

                            if (remainder.isEmpty()) {
                                return
                            } else {
                                outputStream.writeChunk(cipher, remainder + complementary)
                            }

                            remainder = pair?.second ?: ByteArray(0)
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            context.restoreBackupIfExists(fileName)
            throw exception
        } finally {
            context.deleteBackupIfExists(fileName)
        }
    }

    fun decrypt(data: String, iv: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        }

        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData, Charsets.UTF_8)
    }

    fun decryptFromFile(context: Context, fileName: String, chunkIndex: Int): String? {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        return context.openFileInput(fileName).use { inputStream ->
            inputStream.skipChunks(chunkIndex)
            inputStream.readChunk(cipher)?.let { String(it, Charsets.UTF_8) }
        }
    }

    fun deleteKey() = keyStore.deleteEntry(KEY_ALIAS)

    /**
     * Gets the secret (private) key from android key store for encryption/decryption.
     * If the key does not exist, launches [createSecretKey] to create and store a new key.
     */
    private fun getSecretKey(): SecretKey {
        val keyEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return keyEntry?.secretKey ?: createSecretKey()
    }

    /**
     * Creates and stores a secret (private) key in android key store for encryption/decryption.
     */
    private fun createSecretKey(): SecretKey {
        val keyGenSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setKeySize(KEY_SIZE)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(false)
            .build()

        return KeyGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE).apply {
            init(keyGenSpec)
        }.generateKey()
    }

    private fun createIv(): ByteArray {
        val secureRandom = SecureRandom()
        val iv = ByteArray(IV_SIZE)
        secureRandom.nextBytes(iv)
        return iv
    }

    private fun getEncryptedDataSize(dataSize: Int) = dataSize + 16 - (dataSize % 16)

    /**
     * @return Number of bytes written
     * @throws IOException If an I/O error occurs
     */
    private fun FileOutputStream.writeChunk(
        cipher: Cipher,
        data: ByteArray,
        offset: Int = 0,
        chunkSize: Int = min(CHUNK_SIZE, data.size - offset)
    ): Int {
        val iv = createIv()

        write(chunkSize.toByteArray())
        write(iv)

        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        write(cipher.doFinal(data, offset, chunkSize))

        return chunkSize
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    private fun FileInputStream.readChunk(cipher: Cipher): ByteArray? {
        val chunkSize = readBytes(4)?.toInt() ?: return null
        val iv = readBytes(IV_SIZE) ?: return null

        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        val encryptedChunk = readBytes(getEncryptedDataSize(chunkSize))

        return cipher.doFinal(encryptedChunk)
    }

    private fun FileInputStream.skipChunks(count: Int) {
        repeat(count) {
            val chunkSize = readBytes(4)?.toInt() ?: return
            skip(getEncryptedDataSize(chunkSize).toLong() + IV_SIZE)
        }
    }
}
