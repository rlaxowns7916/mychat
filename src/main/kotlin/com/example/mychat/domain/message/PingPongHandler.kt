package com.example.mychat.domain.message

import com.example.mychat.domain.location.GlobalMap
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class PingPongHandler(
    private val globalMap: GlobalMap,
) {
    suspend fun handle(message: Message.PingPong) {
        logger.info { "[PingPongSubscriber][Subscribe] refresh ${message.header.userId} connection metadata" }
        globalMap.put(message.header.userId)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
