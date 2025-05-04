package com.example.websocketgateway.domain.auith

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AuthKeyValidator {
    private val objectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())

    fun validateAndGet(key: String): AuthKey {
        val authKey = parse(key)
        val now = LocalDateTime.now()

        if (authKey.isExpired(now)) {
            throw DomainException(DomainErrorType.EXPIRED)
        }

        return authKey
    }

    private fun parse(key: String): AuthKey {
        return try {
            objectMapper.readValue<AuthKey>(key)
        } catch (e: Exception) {
            throw DomainException(DomainErrorType.INVALID_AUTH_KEY)
        }
    }

    companion object {
    }
}
