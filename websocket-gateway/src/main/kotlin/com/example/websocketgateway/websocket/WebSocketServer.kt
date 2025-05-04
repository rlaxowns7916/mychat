package com.example.websocketgateway.websocket

import com.example.websocketgateway.websocket.command.StompCommandHandlerFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class WebSocketServer(
    private val port: Int,
    private val stompCommandHandlerFactory: StompCommandHandlerFactory,
) {
    private val bossGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup()
    private var channel: Channel? = null

    fun start() {
        logger.info { "[WebSocketServer][Start] (port:$port)" }
        try {
            val bootStrap = ServerBootstrap()
            bootStrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(WebSocketServerPipeLineInitializer(stompCommandHandlerFactory))

            channel = bootStrap.bind(port).sync().channel()
            logger.info { "[WebSocketServer][Started] (port:$port)" }

            channel?.closeFuture()?.sync()
        } catch (e: Exception) {
            logger.error(e) { "[WebSocketServer][Start][Fail] (port:$port)" }
            stop()
        }
    }

    fun stop() {
        logger.info { "[WebSocketServer][Stop] (port:$port)" }
        try {
            channel?.close()?.sync()
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
            logger.info { "[WebSocketServer][Stopped] (port:$port)" }
        } catch (e: Exception) {
            logger.error(e) { "[WebSocketServer][Stop][Fail] (port:$port)" }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
