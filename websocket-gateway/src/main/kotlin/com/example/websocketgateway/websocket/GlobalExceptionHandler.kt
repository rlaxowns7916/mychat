package com.example.websocketgateway.websocket

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.example.websocketgateway.websocket.error.ErrorMessage
import io.github.oshai.kotlinlogging.KotlinLogging
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
        val version = ctx.channel().attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).get()
        val errorType = resolveErrorType(cause)
        val responseFrame = errorType.toResponseFrame(version)

        ctx.writeAndFlush(responseFrame).closeSafety()
    }

    private fun resolveErrorType(cause: Throwable): DomainErrorType {
        when (cause) {
            is DomainException -> {
                return cause.errorType
            }
            else -> {
                logger.error(cause) { "[GlobalExceptionHandler][UnExpectedError]" }
                return DomainErrorType.INTERNAL_SERVER_ERROR
            }
        }
    }

    private fun DomainErrorType.toResponseFrame(version: StompVersion): StompFrame {
        val message = ErrorMessage(this)
        return StompFrameBuilder<ErrorMessage>(
            version = version,
            command = StompCommand.ERROR,
        ).content(message).build()
    }

    private fun ChannelFuture.closeSafety() {
        this.addListener { future ->
            if (future.isSuccess) {
                logger.info { "[GlobalExceptionHandler][SendErrorFrame][Success]" }
            } else {
                logger.error(future.cause()) { "[GlobalExceptionHandler][SendErrorFrame][Fail]" }
            }

            this.channel().close().addListener { closeFuture ->
                if (!closeFuture.isSuccess) {
                    logger.warn { "[GlobalExceptionHandler][CloseChannel][Fail] - Attempting force close" }
                    try {
                        this.channel().unsafe().closeForcibly()
                    } catch (e: Exception) {
                        logger.error(e) { "[GlobalExceptionHandler][ForceClose][Fail]" }
                    }
                }
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
