package com.example.websocketgateway.domain.auth

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class AuthKeyValidatorTest {
    private val objectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
    private val sut = AuthKeyValidator()

    @Test
    fun `유효한 authKey면 AuthKey 객체를 반환한다`() {
        // given
        val futureTime = LocalDateTime.now().plusMinutes(10)
        val key = objectMapper.writeValueAsString(AuthKey("user-1", futureTime))

        // when
        val result = sut.validateAndGet(key)

        // then
        assertEquals("user-1", result.userId)
        assertEquals(futureTime, result.expiredAt)
    }

    @Test
    fun `만료된 authKey면 DomainException(EXPIRED)를 던진다`() {
        // given
        val pastTime = LocalDateTime.now().minusMinutes(5)
        val key = objectMapper.writeValueAsString(AuthKey("user-1", pastTime))

        // expect
        val exception =
            assertThrows<DomainException> {
                sut.validateAndGet(key)
            }

        assertEquals(DomainErrorType.EXPIRED, exception.errorType)
    }

    @Test
    fun `역직렬화에 실패하면 DomainException(INVALID_AUTH_KEY)를 던진다`() {
        // given
        val invalidKey = "this-is-not-json"

        // expect
        val exception =
            assertThrows<DomainException> {
                sut.validateAndGet(invalidKey)
            }

        assertEquals(DomainErrorType.INVALID_AUTH_KEY, exception.errorType)
    }
}
