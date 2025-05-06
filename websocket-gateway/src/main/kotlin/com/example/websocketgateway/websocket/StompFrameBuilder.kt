package com.example.websocketgateway.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.netty.buffer.Unpooled
import io.netty.handler.codec.stomp.DefaultStompFrame
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompFrame
import io.netty.handler.codec.stomp.StompHeaders
import java.nio.charset.StandardCharsets

class StompFrameBuilder<T>(
    private val version: StompVersion,
    private var command: StompCommand,
) {
    private val headers: MutableMap<String, String> = mutableMapOf()
    private var content: T? = null

    fun header(
        key: String,
        value: String,
    ) = apply { headers[key] = value }

    fun headers(headers: Map<String, String>) = apply { this.headers.putAll(headers) }

    fun content(content: T) = apply { this.content = content }

    fun build(): StompFrame {
        val stompBody =
            content?.let {
                val serialized = OBJECT_MAPPER.writeValueAsString(it)
                headers["content-length"] = serialized.length.toString()
                Unpooled.copiedBuffer(serialized, StandardCharsets.UTF_8)
            } ?: Unpooled.EMPTY_BUFFER

        return DefaultStompFrame(command, stompBody).apply {
            headers().apply {
                set(StompHeaders.VERSION, version.subProtocol)
                set(StompHeaders.CONTENT_TYPE, "application/json")
                headers.forEach { (k, v) -> headers().set(k, v) }
            }
        }
    }

    companion object {
        private val OBJECT_MAPPER = jacksonObjectMapper().registerKotlinModule()
    }
}
