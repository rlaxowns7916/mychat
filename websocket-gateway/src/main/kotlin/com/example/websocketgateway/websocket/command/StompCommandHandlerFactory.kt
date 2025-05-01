package com.example.websocketgateway.websocket.command

import io.netty.handler.codec.stomp.StompCommand
import org.springframework.stereotype.Component

@Component
class StompCommandHandlerFactory {
    fun create(command: StompCommand): StompCommandHandler {
        when (command) {
            StompCommand.STOMP -> TODO()
            StompCommand.CONNECT -> TODO()
            StompCommand.CONNECTED -> TODO()
            StompCommand.SEND -> TODO()
            StompCommand.SUBSCRIBE -> TODO()
            StompCommand.UNSUBSCRIBE -> TODO()
            StompCommand.ACK -> TODO()
            StompCommand.NACK -> TODO()
            StompCommand.BEGIN -> TODO()
            StompCommand.ABORT -> TODO()
            StompCommand.COMMIT -> TODO()
            StompCommand.DISCONNECT -> TODO()
            StompCommand.MESSAGE -> TODO()
            StompCommand.RECEIPT -> TODO()
            StompCommand.ERROR -> TODO()
            StompCommand.UNKNOWN -> TODO()
        }
    }
}
