package com.example.websocketgateway.supports

fun ByteArray.toPrettyFormat(): String {
    val sb = StringBuilder()
    for (i in indices step 16) {
        val chunk = sliceArray(i until minOf(i + 16, size))
        val hexPart = chunk.joinToString(" ") { "%02X".format(it) }
        val asciiPart =
            chunk.map { if (it in 32..126) it.toInt().toChar() else '.' }
                .joinToString("")

        sb.append("%-48s | %s\n".format(hexPart, asciiPart))
    }
    return sb.toString()
}
