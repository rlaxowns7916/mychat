package com.example.websocketgateway.websocket.command

import com.example.websocketgateway.websocket.StompFrameBuilder
import com.example.websocketgateway.websocket.StompVersion
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompFrame
import java.util.concurrent.CompletableFuture

/**
 * TODO: SessionMap 갱신
 */
class StompConnectHandler(
    private val version: StompVersion,
) : StompCommandHandler(version) {
    override fun handle(frame: StompFrame): CompletableFuture<StompFrame> {
        val connectionFrame = connectedFrame()
        logger.info { "[StompConnectHandler][onConnect] (responseFrame: $connectionFrame)" }

        return CompletableFuture.completedFuture(connectedFrame())
    }

    private fun connectedFrame(): StompFrame {
        return StompFrameBuilder<Unit>(version, StompCommand.CONNECTED).build()
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
