package com.example.websocketgateway.domain.exception

import java.lang.RuntimeException

class DomainException(errorType: DomainErrorType) : RuntimeException(errorType.message)
