package com.example.securenotes.data.home

fun Int.toByteArray() = ByteArray(4) { i ->
    (this ushr (i * 8)).toByte()
}

fun ByteArray.toInt(): Int {
    var result = 0
    repeat(4) { i ->
        result = result or (this[i].toInt() and 0xFF shl (i * 8))
    }
    return result
}

/**
 * @param index End of the first part (exclusive)
 */
fun ByteArray.split(index: Int): Pair<ByteArray?, ByteArray?> {
    return if (index <= 0) {
        null to this
    } else if (index >= size) {
        this to null
    } else {
        copyOfRange(0, index) to copyOfRange(index, size)
    }
}
