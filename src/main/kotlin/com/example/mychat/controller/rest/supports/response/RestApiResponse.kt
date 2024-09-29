package com.example.mychat.controller.rest.supports.response

import com.example.mychat.supports.error.ErrorType

sealed interface RestApiResponse<T> {
    val responseType: ResponseType
    val data: T?
    val error: ErrorResponse?

    data class Success<T>(override val data: T?) : RestApiResponse<T> {
        override val responseType: ResponseType = ResponseType.SUCCESS
        override val error: ErrorResponse? = null
    }

    data class Error<T>(override val error: ErrorResponse) : RestApiResponse<T> {
        override val responseType: ResponseType = ResponseType.ERROR
        override val data: T? = null

        constructor(errorType: ErrorType, details: Any?) : this(
            ErrorResponse(
                errorType.code,
                errorType.externalMessage,
                details,
            ),
        )
    }
}
