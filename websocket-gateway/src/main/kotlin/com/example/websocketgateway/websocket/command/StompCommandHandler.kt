package com.example.websocketgateway.websocket.command

import com.example.websocketgateway.websocket.StompVersion
import io.netty.handler.codec.stomp.StompFrame
import java.util.concurrent.CompletableFuture

abstract class StompCommandHandler(
    private val version: StompVersion,
) {
    abstract fun handle(frame: StompFrame): CompletableFuture<StompFrame>
}
