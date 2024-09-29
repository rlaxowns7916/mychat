package com.example.mychat.domain.message

import org.springframework.stereotype.Component

@Component
class PingPongHandler {
    suspend fun handle(message: Message.Ping) {
    }
}
