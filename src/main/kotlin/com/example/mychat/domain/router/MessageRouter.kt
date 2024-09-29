package com.example.mychat.domain.router

import com.example.mychat.domain.message.ChattingHandler
import com.example.mychat.domain.message.Message
import com.example.mychat.domain.message.MessageConverter
import com.example.mychat.domain.message.MessageHeader
import com.example.mychat.domain.message.MessageType
import com.example.mychat.domain.message.PingPongHandler
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
                chattingHandler.handle(message)
            }
            MessageType.PING -> {
                val message = MessageConverter.deserialize<Message.Ping>(payload)
                pingPongHandler.handle(message)
            }
        }
    }
}
