package com.example.websocketgateway.domain.auth

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AuthKeyValidator {
    private val objectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())

    fun validateAndGet(key: String): AuthKey {
        // AIDEV-NOTE: JSON 파싱 실패와 만료 시간 검증을 분리하여 명확한 에러 타입 제공
        return try {
            val authKey = objectMapper.readValue(key, AuthKey::class.java)

            if (authKey.isExpired(LocalDateTime.now())) {
                throw DomainException(DomainErrorType.EXPIRED)
            }

            authKey
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException(DomainErrorType.INVALID_AUTH_KEY)
        }
    }
}
