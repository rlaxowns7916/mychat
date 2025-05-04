package com.example.websocketgateway.websocket.error

import com.example.websocketgateway.domain.exception.DomainErrorType

data class ErrorMessage(
    val code: String,
    val message: String,
) {
    constructor(type: DomainErrorType) : this(
        code = type.code.toString(),
        message = type.message,
    )
}
