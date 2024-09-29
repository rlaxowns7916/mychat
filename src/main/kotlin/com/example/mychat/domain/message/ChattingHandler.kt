package com.example.mychat.domain.message

import com.example.mychat.domain.location.GlobalMap
import com.example.mychat.storage.redis.PubSubManager
import org.springframework.stereotype.Component

@Component
class ChattingHandler(
    private val globalMap: GlobalMap,
    private val pubSubManager: PubSubManager,
) {
    suspend fun handle(message: Message.Chat) {
        val routingDestination = globalMap.get(message.body.to) ?: throw IllegalArgumentException("Destination not found")
        pubSubManager.publish(routingDestination.toChannel(MessageType.CHAT), String(MessageConverter.serialize<Message.Chat>(message)))
    }
}
