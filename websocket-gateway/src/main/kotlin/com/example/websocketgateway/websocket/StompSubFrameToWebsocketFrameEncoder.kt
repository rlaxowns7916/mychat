package com.example.websocketgateway.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.stomp.LastStompContentSubframe
import io.netty.handler.codec.stomp.StompContentSubframe
import io.netty.handler.codec.stomp.StompFrame
import io.netty.handler.codec.stomp.StompHeaders
import io.netty.handler.codec.stomp.StompHeadersSubframe
import io.netty.handler.codec.stomp.StompSubframe
import io.netty.handler.codec.stomp.StompSubframeEncoder

class StompSubFrameToWebsocketFrameEncoder : StompSubframeEncoder() {
    public override fun encode(
        ctx: ChannelHandlerContext,
        msg: StompSubframe,
        out: MutableList<Any>,
    ) {
        super.encode(ctx, msg, out)
    }

    override fun convertFullFrame(
        original: StompFrame,
        encoded: ByteBuf,
    ): WebSocketFrame {
        if (isTextFrame(original)) {
            return TextWebSocketFrame(encoded)
        }

        return BinaryWebSocketFrame(encoded)
    }

    override fun convertHeadersSubFrame(
        original: StompHeadersSubframe,
        encoded: ByteBuf,
    ): WebSocketFrame {
        if (isTextFrame(original)) {
            return TextWebSocketFrame(false, 0, encoded)
        }

        return BinaryWebSocketFrame(false, 0, encoded)
    }

    override fun convertContentSubFrame(
        original: StompContentSubframe,
        encoded: ByteBuf,
    ): WebSocketFrame {
        if (original is LastStompContentSubframe) {
            return ContinuationWebSocketFrame(true, 0, encoded)
        }

        return ContinuationWebSocketFrame(false, 0, encoded)
    }

    private fun isTextFrame(headersSubframe: StompHeadersSubframe): Boolean {
        val contentType = headersSubframe.headers().getAsString(StompHeaders.CONTENT_TYPE)
        return contentType != null && (contentType.startsWith("text") || contentType.startsWith("application/json"))
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
