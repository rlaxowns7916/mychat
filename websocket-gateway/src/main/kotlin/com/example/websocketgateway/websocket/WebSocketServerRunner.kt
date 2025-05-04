package com.example.websocketgateway.websocket

import com.example.websocketgateway.websocket.command.StompCommandHandlerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class WebSocketServerRunner(
    @Value("\${server.websocket-port}")
    private val port: Int,
    private val stompCommandHandlerFactory: StompCommandHandlerFactory,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val webSocketServer = WebSocketServer(port, stompCommandHandlerFactory)

        webSocketServer.start()
    }
}
