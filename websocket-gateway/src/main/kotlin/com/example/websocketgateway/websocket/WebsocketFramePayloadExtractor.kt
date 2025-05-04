package com.example.websocketgateway.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class WebsocketFramePayloadExtractor : MessageToMessageDecoder<WebSocketFrame>() {
    override fun decode(
        ctx: ChannelHandlerContext,
        msg: WebSocketFrame,
        out: MutableList<Any>,
    ) {
        out.add(msg.content().retain())
    }
}
