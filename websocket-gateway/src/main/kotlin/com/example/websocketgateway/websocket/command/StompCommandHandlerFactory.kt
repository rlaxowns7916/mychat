package com.example.websocketgateway.websocket.command

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.example.websocketgateway.websocket.StompVersion
import io.netty.handler.codec.stomp.StompCommand
import org.springframework.stereotype.Component

@Component
class StompCommandHandlerFactory {
    fun create(
        version: StompVersion,
        command: StompCommand,
    ): StompCommandHandler {
        return when (command) {
            StompCommand.CONNECT -> StompConnectHandler(version)
            StompCommand.SEND -> TODO()
            StompCommand.SUBSCRIBE -> TODO()
            StompCommand.UNSUBSCRIBE -> TODO()
            StompCommand.ACK -> TODO()
            StompCommand.NACK -> TODO()
            StompCommand.DISCONNECT -> TODO()
            StompCommand.MESSAGE -> TODO()
            StompCommand.STOMP -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.CONNECTED -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.BEGIN -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.ABORT -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.COMMIT -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.RECEIPT -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.ERROR -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
            StompCommand.UNKNOWN -> throw DomainException(DomainErrorType.UNSUPPORTED_OPERATION)
        }
    }
}
