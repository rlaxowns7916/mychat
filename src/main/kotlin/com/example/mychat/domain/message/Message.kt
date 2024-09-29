package com.example.mychat.domain.message

import com.example.mychat.domain.message.model.ChatBody
import com.example.mychat.domain.message.model.MessageHeader

sealed interface Message {
    val header: MessageHeader

    data class Ping(override val header: MessageHeader, val body: String) : Message

    data class Chat(override val header: MessageHeader, val body: ChatBody) : Message
}
