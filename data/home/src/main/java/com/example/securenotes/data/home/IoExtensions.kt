package com.example.securenotes.data.home

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

fun Context.backupFileIfExists(fileName: String) {
    val destination = File(filesDir, fileName)
    if (!destination.exists()) {
        return
    }

    val backup = File(filesDir, "backup - $fileName")
    if (backup.exists()) {
        backup.delete()
    }

    destination.copyTo(backup)
}

fun Context.restoreBackupIfExists(fileName: String) {
    val backup = File(filesDir, "backup - $fileName")
    if (!backup.exists()) {
        return
    }

    val destination = File(filesDir, fileName)
    if (destination.exists()) {
        destination.delete()
    }

    backup.copyTo(destination)
    backup.delete()
}

fun Context.deleteBackupIfExists(fileName: String) {
    val backup = File(filesDir, "backup - $fileName")
    if (backup.exists()) {
        backup.delete()
    }
}

/**
 * @throws FileNotFoundException If backup file is not found with the given [fileName]
 */
fun Context.openFileBackup(fileName: String): FileInputStream {
    val backup = File(filesDir, "backup - $fileName")
    if (!backup.exists()) {
        throw FileNotFoundException("Backup file not found")
    }

    return backup.inputStream()
}

fun FileInputStream.readBytes(length: Int): ByteArray? {
    val buffer = ByteArray(length)
    val readBytes = read(buffer)
    return buffer.takeIf { readBytes > -1 }
}
