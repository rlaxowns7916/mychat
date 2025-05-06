package com.example.websocketgateway.websocket.command

import com.example.websocketgateway.supports.NettyLogger
import com.example.websocketgateway.supports.TraceContext
import com.example.websocketgateway.websocket.StompFrameBuilder
import com.example.websocketgateway.websocket.StompVersion
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompFrame
import java.util.concurrent.CompletableFuture

/**
 * TODO: SessionMap 갱신
 */
class StompConnectHandler(
    private val version: StompVersion,
) : StompCommandHandler(version) {
    override fun handle(
        traceContext: TraceContext,
        frame: StompFrame,
    ): CompletableFuture<StompFrame> {
        val connectionFrame = connectedFrame()
        logger.info(traceContext) { "[StompConnectHandler][onConnect] (responseFrame: $connectionFrame)" }

        return CompletableFuture.completedFuture(connectedFrame())
    }

    private fun connectedFrame(): StompFrame {
        return StompFrameBuilder<Unit>(version, StompCommand.CONNECTED).build()
    }

    companion object {
        private val logger = NettyLogger.getLogger(StompConnectHandler::class.java)
    }
}
