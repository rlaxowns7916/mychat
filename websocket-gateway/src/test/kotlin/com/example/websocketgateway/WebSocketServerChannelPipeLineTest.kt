package com.example.websocketgateway

import com.example.websocketgateway.websocket.StompMessageHandler
import com.example.websocketgateway.websocket.StompProtocolHandler
import com.example.websocketgateway.websocket.StompSubFrameToWebsocketFrameEncoder
import com.example.websocketgateway.websocket.StompVersion
import com.example.websocketgateway.websocket.WebSocketServerPipeLineInitializer
import com.example.websocketgateway.websocket.WebsocketFramePayloadExtractor
import com.example.websocketgateway.websocket.command.StompCommandHandlerFactory
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.http.EmptyHttpHeaders
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.codec.stomp.StompSubframeAggregator
import io.netty.handler.codec.stomp.StompSubframeDecoder
import io.netty.handler.codec.stomp.StompSubframeEncoder
import io.netty.handler.timeout.IdleStateHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WebSocketServerChannelPipeLineTest {
    @Test
    fun `WebSocket Upgrade 이전 ChannelPipeLine 검증`() {
        // given
        val stompMessageHandler = StompMessageHandler(StompCommandHandlerFactory())
        val webSocketServerInitializer = WebSocketServerPipeLineInitializer(stompMessageHandler)
        val channel = EmbeddedChannel(webSocketServerInitializer)

        val pipeline = channel.pipeline()

        // when
        channel.pipeline().fireChannelActive()

        // then
        val handlers = pipeline.toList().map { it.value }
        assertTrue(handlers.any { it is HttpServerCodec }, "HttpServerCodec 누락")
        assertTrue(handlers.any { it is HttpObjectAggregator }, "HttpObjectAggregator 누락")
        assertTrue(handlers.any { it is WebSocketServerCompressionHandler }, "WebSocketServerCompressionHandler 누락")
        assertTrue(handlers.any { it is WebSocketServerProtocolHandler }, "WebSocketServerProtocolHandler 누락")
        assertTrue(handlers.any { it is IdleStateHandler }, "IdleStateHandler 누락")
        assertTrue(handlers.any { it is StompProtocolHandler }, "StompProtocolHandler 누락")

        val handlerTypes = handlers.map { it::class }
        val httpCodecIndex = handlerTypes.indexOf(HttpServerCodec::class)
        val httpAggregatorIndex = handlerTypes.indexOf(HttpObjectAggregator::class)
        val wsCompressionIndex = handlerTypes.indexOf(WebSocketServerCompressionHandler::class)
        val wsProtocolIndex = handlerTypes.indexOf(WebSocketServerProtocolHandler::class)

        assertTrue(httpCodecIndex < httpAggregatorIndex, "HttpServerCodec는 HttpObjectAggregator 앞에 위치해야 함")
        assertTrue(httpAggregatorIndex < wsCompressionIndex, "HttpObjectAggregator는 WebSocketServerCompressionHandler 앞에 위치해야 함")
        assertTrue(wsCompressionIndex < wsProtocolIndex, "WebSocketServerCompressionHandler는 WebSocketServerProtocolHandler 앞에 위치해야 함")
    }

    @Test
    fun `WebSocket Upgrade 이후 ChannelPipeLine 검증`() {
        // given
        val stompMessageHandler = StompMessageHandler(StompCommandHandlerFactory())
        val webSocketServerInitializer = WebSocketServerPipeLineInitializer(stompMessageHandler)
        val channel = EmbeddedChannel(webSocketServerInitializer)

        val pipeline = channel.pipeline()

        // when
        channel.pipeline().fireChannelActive()
        channel.pipeline().fireUserEventTriggered(
            WebSocketServerProtocolHandler.HandshakeComplete(
                "/chat",
                EmptyHttpHeaders.INSTANCE,
                StompVersion.VERSION1_2.subProtocol,
            ),
        )

        val handlers = pipeline.toList().map { it.value }
        assertTrue(handlers.any { it is WebSocketFrameAggregator }, "WebSocketFrameAggregator 누락")
        assertTrue(handlers.any { it is StompSubFrameToWebsocketFrameEncoder }, "StompSubFrameToWebsocketFrameEncoder 누락")
        assertTrue(handlers.any { it is WebsocketFramePayloadExtractor }, "WebsocketFrameToStompSubFrameDecoder 누락")
        assertTrue(handlers.any { it is StompSubframeEncoder }, "StompSubframeEncoder 누락")
        assertTrue(handlers.any { it is StompSubframeDecoder }, "StompSubframeDecoder 누락")
        assertTrue(handlers.any { it is StompSubframeAggregator }, "StompSubframeAggregator 누락")
        assertTrue(handlers.any { it is StompMessageHandler }, "StompMessageHandler 누락")

        val handlerTypes = handlers.map { it::class }

        val wsAggregatorIndex = handlerTypes.indexOf(WebSocketFrameAggregator::class)
        val stompToWsEncoderIndex = handlerTypes.indexOf(StompSubFrameToWebsocketFrameEncoder::class)
        val wsFramePayloadExtractorIndex = handlerTypes.indexOf(WebsocketFramePayloadExtractor::class)
        val stompDecoderIndex = handlerTypes.indexOf(StompSubframeDecoder::class)
        val stompAggregatorIndex = handlerTypes.indexOf(StompSubframeAggregator::class)
        val stompMessageHandlerIndex = handlerTypes.indexOf(StompMessageHandler::class)

        assertTrue(
            wsAggregatorIndex < stompToWsEncoderIndex,
            "WebSocketFrameAggregator가 StompSubFrameToWebsocketFrameEncoder 앞에 와야 함",
        )
        assertTrue(
            stompToWsEncoderIndex < wsFramePayloadExtractorIndex,
            "StompSubFrameToWebsocketFrameEncoder가 WebsocketFrameToStompSubFrameDecoder 앞에 와야 함",
        )
        assertTrue(stompDecoderIndex < stompAggregatorIndex, "StompSubframeDecoder가 StompSubframeAggregator 앞에 와야 함")
        assertTrue(
            stompAggregatorIndex < stompMessageHandlerIndex,
            "StompSubframeAggregator가 StompMessageHandler 앞에 와야 함",
        )
    }
}
