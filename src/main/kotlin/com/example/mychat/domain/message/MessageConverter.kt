package com.example.mychat.domain.message

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object MessageConverter {
    val objectMapper = jacksonObjectMapper()

    fun deserializeHeader(bytes: ByteArray): MessageHeader {
        return objectMapper.readValue(bytes, MessageHeader::class.java)
    }

    inline fun <reified T> deserialize(bytes: ByteArray): T {
        val typeRef = object : TypeReference<T>() {}
        return objectMapper.readValue(bytes, typeRef)
    }
}
