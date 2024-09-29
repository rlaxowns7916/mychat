package com.example.mychat.domain.message

import com.example.mychat.domain.message.model.MessageHeader
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object MessageConverter {
    val objectMapper: ObjectMapper =
        jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    inline fun <reified T> serialize(message: T): ByteArray {
        return objectMapper.writeValueAsBytes(message)
    }

    fun deserializeHeader(bytes: ByteArray): MessageHeader {
        val rootNode = objectMapper.readTree(bytes)
        val headerNode = rootNode["header"]

        return objectMapper.treeToValue(headerNode, MessageHeader::class.java)
    }

    inline fun <reified T> deserialize(bytes: ByteArray): T {
        val typeRef = object : TypeReference<T>() {}
        return objectMapper.readValue(bytes, typeRef)
    }

    inline fun <reified T> deserialize(string: String): T {
        val typeRef = object : TypeReference<T>() {}
        return objectMapper.readValue(string, typeRef)
    }
}
