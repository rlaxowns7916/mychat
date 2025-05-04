package com.example.websocketgateway.websocket.command

import com.example.websocketgateway.domain.auith.AuthKeyValidator
import com.example.websocketgateway.websocket.StompVersion
import io.netty.handler.codec.stomp.StompCommand
import org.springframework.stereotype.Component

@Component
class StompCommandHandlerFactory(
    private val authKeyValidator: AuthKeyValidator,
) {
    fun create(
        version: StompVersion,
        command: StompCommand,
    ): StompCommandHandler {
        return when (command) {
            StompCommand.CONNECT -> ConnectHandler(version)
            StompCommand.SEND -> TODO()
            StompCommand.SUBSCRIBE -> TODO()
            StompCommand.UNSUBSCRIBE -> TODO()
            StompCommand.ACK -> TODO()
            StompCommand.NACK -> TODO()
            StompCommand.DISCONNECT -> TODO()
            StompCommand.MESSAGE -> TODO()
            StompCommand.ERROR -> TODO()
            else -> {
                TODO("UnsupportedCommand Handler")
            }
        }
    }
}
