package com.example.websocketgateway.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class WebsocketFrameToStompSubFrameDecoder : MessageToMessageDecoder<WebSocketFrame>() {
    override fun decode(
        ctx: ChannelHandlerContext,
        msg: WebSocketFrame,
        out: MutableList<Any>,
    ) {
        out.add(msg.content().retain())
        logger.info { "[WebsocketFrameToStompSubFrameDecoder] WebSocketFrame payload -> StompSubFrame" }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
