package com.example.websocketgateway.websocket.command

import io.netty.handler.codec.stomp.StompFrame

interface StompCommandHandler {
    fun handle(frame: StompFrame): StompFrame
}
