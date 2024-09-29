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
                    "type": "CHAT",
                    "userId": "taejun1"
                },
                "body": {
                    "to": "taejun2",
                    "payload": "hello"
                }
            }
            """.trimIndent()

        val actual = assertDoesNotThrow { MessageConverter.deserializeHeader(given.toByteArray()) }
        assertThat(actual.type).isEqualTo(MessageType.CHAT)
        assertThat(actual.userId).isEqualTo("taejun1")
    }

    @Test
    fun deserializeBody() {
        val given =
            """
            {
                "header": {
                    "type": "CHAT",
                    "userId": "taejun1"
                },
                "body": {
                    "to": "taejun2",
                    "payload": "hello"
                }
            }
            """.trimIndent()

        val actual = assertDoesNotThrow { MessageConverter.deserialize<Message.Chat>(given.toByteArray()) }
        assertThat(actual.header.userId).isEqualTo("taejun1")
        assertThat(actual.header.type).isEqualTo(MessageType.CHAT)

        assertThat(actual.body.to).isEqualTo("taejun2")
        assertThat(actual.body.payload).isEqualTo("hello")
    }
}
