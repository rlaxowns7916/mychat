package com.example.mychat.domain.message

import com.example.mychat.domain.message.model.ChatBody
import com.example.mychat.domain.message.model.MessageHeader
import com.example.mychat.domain.message.model.PingPongBody

sealed interface Message {
    val header: MessageHeader

    data class Chat(override val header: MessageHeader, val body: ChatBody) : Message

    data class PingPong(override val header: MessageHeader, val body: PingPongBody) : Message
}
