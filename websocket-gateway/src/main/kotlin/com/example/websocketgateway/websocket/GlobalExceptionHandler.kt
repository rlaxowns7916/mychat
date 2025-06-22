package com.example.websocketgateway.websocket

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.example.websocketgateway.supports.NettyLogger
import com.example.websocketgateway.supports.TraceContext
import com.example.websocketgateway.websocket.error.ErrorMessage
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompFrame

class GlobalExceptionHandler : ChannelInboundHandlerAdapter() {
    override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        val traceContext =
            ctx.channel().attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).get()
                ?: TraceContext.root()
        val version =
            ctx.channel().attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).get()
                ?: StompVersion.VERSION1_2

        val errorType =
            when (cause) {
                is DomainException -> {
                    cause.errorType
                }
                else -> {
                    logger.error(traceContext, cause) { "[GlobalExceptionHandler][UnExpectedError]" }
                    DomainErrorType.INTERNAL_SERVER_ERROR
                }
            }

        ctx.writeAndFlush(errorType.toResponseFrame(version)).closeSafety()
    }

    private fun DomainErrorType.toResponseFrame(version: StompVersion): StompFrame {
        val message = ErrorMessage(this)
        return StompFrameBuilder<ErrorMessage>(
            version = version,
            command = StompCommand.ERROR,
        ).content(message).build()
    }

    private fun ChannelFuture.closeSafety() {
        val traceContext =
            channel().attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).get()
                ?: TraceContext.root()
        this.addListener { future ->
            if (future.isSuccess) {
                logger.info(traceContext) { "[GlobalExceptionHandler][SendErrorFrame][Success]" }
            } else {
                logger.error(traceContext, future.cause()) { "[GlobalExceptionHandler][SendErrorFrame][Fail]" }
            }

            this.channel().close().addListener { closeFuture ->
                if (!closeFuture.isSuccess) {
                    try {
                        this.channel().unsafe().closeForcibly()
                    } catch (e: Exception) {
                        logger.error(traceContext, e) { "[GlobalExceptionHandler][ForceClose][Fail]" }
                    }
                }
            }
        }
    }

    companion object {
        private val logger = NettyLogger.getLogger(GlobalExceptionHandler::class.java)
    }
}
