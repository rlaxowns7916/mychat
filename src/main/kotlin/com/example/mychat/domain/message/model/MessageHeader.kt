package com.example.mychat.domain.message.model

import com.example.mychat.domain.message.MessageType

data class MessageHeader(
    val userId: String,
    val type: MessageType,
)
