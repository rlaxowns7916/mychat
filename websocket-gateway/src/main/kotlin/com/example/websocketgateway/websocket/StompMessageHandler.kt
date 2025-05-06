package com.example.websocketgateway.websocket

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.example.websocketgateway.supports.NettyLogger
import com.example.websocketgateway.supports.TraceContext
import com.example.websocketgateway.websocket.command.StompCommandHandlerFactory
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
        val traceContext = ctx.channel().attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).get()

        if (msg.decoderResult().isFailure) {
            logger.error(traceContext, msg.decoderResult().cause()) { "[StompMessageHandler][DecodeFail]" }
            throw DomainException(DomainErrorType.INVALID_FRAME_FORMAT)
        }

        val command = msg.command()
        val stompVersion = ctx.channel().attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).get()

        val handler = commandHandlerFactory.create(stompVersion, command)
        val responseFuture = handler.handle(traceContext, msg)

        responseFuture.whenCompleteAsync({ response, error ->
            if (error != null) {
                ctx.fireExceptionCaught(error)
            } else if (response != null) {
                ctx.channel().writeAndFlush(response)
            }
        }, ctx.channel().eventLoop())
    }

    companion object {
        private val logger = NettyLogger.getLogger(StompMessageHandler::class.java)
    }
}
