package com.example.websocketgateway.domain.exception

import java.lang.RuntimeException

class DomainException(val errorType: DomainErrorType) : RuntimeException(errorType.message)
