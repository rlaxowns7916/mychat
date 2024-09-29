package com.example.mychat.domain.message

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class MessageConverterTest {
    @Test
    fun deserializeHeader() {
        val given =
            """
            {
                "header": {
                    "type": "CHAT"
                },
                "body": {
                    "to": "taejun",
                    "payload": "hello"
                }
            }
            """.trimIndent()

        val actual = assertDoesNotThrow { MessageConverter.deserializeHeader(given.toByteArray()) }
        assertThat(actual.type).isEqualTo(MessageType.CHAT)
    }

    @Test
    fun deserializeBody() {
        val given =
            """
            {
                "header": {
                    "type": "CHAT"
                },
                "body": {
                    "to": "taejun",
                    "payload": "hello"
                }
            }
            """.trimIndent()

        val actual = assertDoesNotThrow { MessageConverter.deserialize<Message.Chat>(given.toByteArray()) }
        assertThat(actual.header.type).isEqualTo(MessageType.CHAT)
        assertThat(actual.body.to).isEqualTo("taejun")
        assertThat(actual.body.payload).isEqualTo("hello")
    }
}
