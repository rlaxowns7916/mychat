package com.example.websocketgateway.websocket.command

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
            else -> {
                TODO("UnsupportedCommand Handler")
            }
        }
    }
}
