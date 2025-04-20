package com.example.websocketgateway.websocket

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.timeout.IdleStateHandler
import java.util.concurrent.TimeUnit

class WebSocketServerPipeLineInitializer() : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val pipeLine = ch.pipeline()
        pipeLine.addLast(HttpServerCodec())
        pipeLine.addLast(HttpObjectAggregator(MAX_CONTENT_LENGTH))
        pipeLine.addLast(WebSocketServerCompressionHandler())
        pipeLine.addLast(WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true))
        pipeLine.addLast(IdleStateHandler(60, 30, 0, TimeUnit.SECONDS))
        pipeLine.addLast(WebSocketHandler())
    }

    companion object {
        private const val WEBSOCKET_PATH = "/connect"
        private const val MAX_CONTENT_LENGTH = 65536
    }
}
