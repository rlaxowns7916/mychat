package com.example.mychat.supports.error

enum class ErrorType(
    val code: Int,
    val externalMessage: String,
) {
    USER_NOT_FOUND(
        4000,
        "사용자를 찾을 수 없습니다",
    ),
    INTERNAL_SERVER_ERROR(
        5000,
        "시스템 처리중 오류가 발생하였습니다",
    ),
}
