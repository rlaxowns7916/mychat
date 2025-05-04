package com.example.websocketgateway.domain.exception

enum class DomainErrorType(
    val code: Int,
    val message: String,
) {
    INTERNAL_SERVER_ERROR(
        code = 1000,
        message = "Internal server error",
    ),
    INVALID_FRAME_FORMAT(
        code = 1001,
        message = "Invalid frame format",
    ),

    INVALID_AUTH_KEY(
        code = 1002,
        message = "Invalid auth key",
    ),

    EXPIRED(
        code = 1003,
        message = "Expired",
    ),
}
