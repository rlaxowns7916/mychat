package com.example.websocketgateway.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler

class WebSocketHandler : SimpleChannelInboundHandler<WebSocketFrame>() {
    private val logger = KotlinLogging.logger {}

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.info { "[WebSocketHandler][Active] (client: ${ctx.channel().remoteAddress()})" }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.info { "[WebSocketHandler][InActive] (client: ${ctx.channel().remoteAddress()})" }
    }

    override fun channelRead0(
        ctx: ChannelHandlerContext,
        frame: WebSocketFrame,
    ) {
        if (frame is TextWebSocketFrame) {
            val request = frame.text()
            logger.info { "[WebSocketHandler][TextFrame] (request: $request)" }
            ctx.channel().writeAndFlush(TextWebSocketFrame("echo: $request"))
        }
    }

    override fun userEventTriggered(
        ctx: ChannelHandlerContext,
        evt: Any,
    ) {
        if (evt is WebSocketServerProtocolHandler.HandshakeComplete) {
            logger.info { "[WebSocketHandler][HandshakeComplete] (client: ${ctx.channel().remoteAddress()}, uri:${evt.requestUri()})" }
            /**
             * TODO: Authentication 검증하기 (Token 검증) && HttpServer에 Sta지e전파 어떻게하
             */
        }
    }

    override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        logger.error(cause) { "[WebSocketHandler][ErrorCaught]" }
        ctx.close()
    }
}
