package com.example.mychat.domain.location

import com.example.mychat.domain.message.MessageType

data class GlobalLocation(
    val identifier: String,
) {
    fun toChannel(messageType: MessageType): String {
        return "channel:${messageType.name}:$identifier"
    }
}
