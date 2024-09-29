package com.example.mychat.domain.message

sealed interface Message {
    val header: MessageHeader

    data class Ping(override val header: MessageHeader, val body: String) : Message

    data class Chat(override val header: MessageHeader, val to: String, val body: String) : Message
}
