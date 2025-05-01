package com.example.websocketgateway.websocket

import io.netty.util.AttributeKey

enum class StompVersion(
    val version: String,
    val subProtocol: String,
) {
    VERSION1_0("1.0", "v10.stomp"),
    VERSION1_1("1.1", "v11.stomp"),
    VERSION1_2("1.2", "v12.stomp"),
    ;

    companion object {
        val CHANNEL_ATTRIBUTE_KEY: AttributeKey<StompVersion> = AttributeKey.valueOf<StompVersion>(this::class.java.name)

        fun getSupportedSubProtocols(): String {
            return entries.joinToString(",") { it.subProtocol }
        }

        fun fromSubProtocol(subProtocol: String): StompVersion {
            return entries.firstOrNull { it.subProtocol == subProtocol }
                ?: throw IllegalArgumentException("Unsupported STOMP version: $subProtocol")
        }
    }
}
