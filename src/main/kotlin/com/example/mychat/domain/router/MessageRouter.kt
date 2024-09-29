package com.example.mychat.domain.router

import com.example.mychat.domain.message.ChattingHandler
import com.example.mychat.domain.message.Message
import com.example.mychat.domain.message.MessageConverter
import com.example.mychat.domain.message.MessageType
import com.example.mychat.domain.message.PingPongHandler
import com.example.mychat.domain.message.model.MessageHeader
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class MessageRouter(
    private val pingPongHandler: PingPongHandler,
    private val chattingHandler: ChattingHandler,
) {
    suspend fun route(
        header: MessageHeader,
        payload: ByteArray,
    ) {
        val type = header.type
        when (type) {
            MessageType.CHAT -> {
                val message = MessageConverter.deserialize<Message.Chat>(payload)
                logger.info { "[MessageRouter][route] (header: $header, message: $message, payload: $payload)" }
                chattingHandler.handle(message)
            }
            MessageType.PING -> {
                val message = MessageConverter.deserialize<Message.Ping>(payload)
                pingPongHandler.handle(message)
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
