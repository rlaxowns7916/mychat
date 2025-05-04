package com.example.websocketgateway.websocket.command

import com.example.websocketgateway.websocket.StompFrameBuilder
import com.example.websocketgateway.websocket.StompVersion
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompFrame
import java.util.concurrent.CompletableFuture

/**
 * TODO: SessionMap 갱신
 */
class ConnectHandler(
    private val version: StompVersion,
) : StompCommandHandler(version) {
    override fun handle(frame: StompFrame): CompletableFuture<StompFrame> {
        return CompletableFuture.completedFuture(connectedFrame())
    }

    private fun connectedFrame(): StompFrame {
        return StompFrameBuilder<Unit>(version, StompCommand.CONNECTED).build()
    }
}
