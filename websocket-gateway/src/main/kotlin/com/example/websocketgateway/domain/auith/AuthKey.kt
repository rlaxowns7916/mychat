package com.example.websocketgateway.domain.auith

import java.time.LocalDateTime

data class AuthKey(
    val userId: String,
    val expiredAt: LocalDateTime,
) {
    fun isExpired(now: LocalDateTime): Boolean {
        return expiredAt.isBefore(now)
    }
}
