package com.example.websocketgateway.websocket

import com.example.websocketgateway.websocket.command.StompCommandHandlerFactory
import com.example.websocketgateway.websocket.command.StompErrorCommandHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.stomp.StompFrame

class StompMessageHandler(
    private val commandHandlerFactory: StompCommandHandlerFactory,
) : SimpleChannelInboundHandler<StompFrame>() {
    override fun channelRead0(
        ctx: ChannelHandlerContext,
        msg: StompFrame,
    ) {
        val handler =
            if (msg.decoderResult().isSuccess) {
                val command = msg.command()
                commandHandlerFactory.create(command)
            } else {
                StompErrorCommandHandler()
            }
        val responseFrame = handler.handle(msg)
        ctx.writeAndFlush(responseFrame)
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
