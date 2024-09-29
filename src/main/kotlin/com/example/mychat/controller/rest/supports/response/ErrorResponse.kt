package com.example.mychat.controller.rest.supports.response

import com.example.mychat.supports.error.ErrorType

data class ErrorResponse(
    val code: Int,
    val message: String,
    val details: Any?,
) {
    constructor(errorType: ErrorType, details: Any?) : this(
        errorType.code,
        errorType.externalMessage,
        details,
    )
}
