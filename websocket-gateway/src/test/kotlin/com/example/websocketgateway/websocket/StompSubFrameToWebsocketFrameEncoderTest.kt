package com.example.websocketgateway.websocket

import com.example.websocketgateway.supports.withEmbeddedChannel
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.stomp.DefaultLastStompContentSubframe
import io.netty.handler.codec.stomp.DefaultStompContentSubframe
import io.netty.handler.codec.stomp.DefaultStompFrame
import io.netty.handler.codec.stomp.DefaultStompHeadersSubframe
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompHeaders
import io.netty.util.CharsetUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StompSubFrameToWebsocketFrameEncoderTest {
    private val sut = StompSubFrameToWebsocketFrameEncoder()

    @Test
    fun `FullFrame을 TextWebSocketFrame으로 파싱 할 수 있다`() =
        withEmbeddedChannel(sut) {
            // given
            val contentString = "{\"message\":\"Hello, World!\"}"
            val content = Unpooled.copiedBuffer(contentString, CharsetUtil.UTF_8)
            val stompFrame =
                DefaultStompFrame(StompCommand.SEND, content).apply {
                    headers().set(StompHeaders.CONTENT_TYPE, "application/json")
                }

            // when
            it.writeOutbound(stompFrame)

            // then
            val actual = it.readOutbound<WebSocketFrame>()
            assertTrue(actual is TextWebSocketFrame, "CONTENT_TYPE: application/json은 TextWebSocketFrame으로 변환되어야 함")

            val actualContents = String(ByteBufUtil.getBytes(actual.content()), CharsetUtil.UTF_8)
            println(actualContents)

            assertTrue(actualContents.contains("SEND"), "FullFrame은 SEND 명령어를 포함해야 함")
            assertTrue(actualContents.contains("application/json"), "FullFrame은 content-type 헤더를 포함해야 함")
            assertTrue(actualContents.contains(contentString))
        }

    @Test
    fun `FullFrame을 BinaryWebSocketFrame으로 파싱 할 수 있다`() =
        withEmbeddedChannel(sut) {
            // given
            val contentString = "{\"message\":\"Hello, World!\"}"
            val content = Unpooled.copiedBuffer(contentString, CharsetUtil.UTF_8)
            val stompFrame =
                DefaultStompFrame(StompCommand.SEND, content)

            // when
            it.writeOutbound(stompFrame)

            // then
            val actual = it.readOutbound<WebSocketFrame>()
            assertTrue(actual is BinaryWebSocketFrame, "CONTENT_TYPE: application/json은 TextWebSocketFrame으로 변환되어야 함")

            val actualContents = String(ByteBufUtil.getBytes(actual.content()), CharsetUtil.UTF_8)
            println(actualContents)

            assertTrue(actualContents.contains("SEND"), "FullFrame은 SEND 명령어를 포함해야 함")
            assertTrue(actualContents.contains(contentString))
        }

    @Test
    fun `SubFreame-Header를 파싱 할 수 있다`() =
        withEmbeddedChannel(sut) {
            // given
            val headersSubframe =
                DefaultStompHeadersSubframe(StompCommand.SEND).apply {
                    headers().set(StompHeaders.CONTENT_TYPE, "application/json")
                    headers().set(StompHeaders.DESTINATION, "/topic/test")
                }

            // when
            it.writeOutbound(headersSubframe)

            // then
            val actual = it.readOutbound<WebSocketFrame>()
            assertTrue(actual is TextWebSocketFrame, "JSON 컨텐츠 타입의 HeadersSubframe은 TextWebSocketFrame으로 변환되어야 함")
            assertFalse((actual as TextWebSocketFrame).isFinalFragment, "HeadersSubframe은 final fragment가 아니어야 함")

            val actualContents = String(ByteBufUtil.getBytes(actual.content()), CharsetUtil.UTF_8)
            println(actualContents)

            assertTrue(actualContents.contains("SEND"), "HeadersSubframe은 SEND 명령어를 포함해야 함")
            assertTrue(actualContents.contains("application/json"), "HeadersSubframe은 content-type 헤더를 포함해야 함")
            assertTrue(actualContents.contains("/topic/test"), "HeadersSubframe은 destination 헤더를 포함해야 함")
        }

    @Test
    fun `SubFrame-Content를 파싱 할 수 있다 (Continue)`() =
        withEmbeddedChannel(sut) {
            // given
            val contentString = "This is content data"
            val contentBuf = Unpooled.copiedBuffer(contentString, CharsetUtil.UTF_8)
            val contentSubframe = DefaultStompContentSubframe(contentBuf)

            // when
            it.writeOutbound(contentSubframe)

            // then
            val actual = it.readOutbound<WebSocketFrame>()
            assertTrue(actual is ContinuationWebSocketFrame, "ContentSubframe은 ContinuationWebSocketFrame으로 변환되어야 함")
            assertFalse((actual as ContinuationWebSocketFrame).isFinalFragment, "LastContentSubframe은 final fragment여야 함")

            val actualContents = String(ByteBufUtil.getBytes(actual.content()), CharsetUtil.UTF_8)
            println(actualContents)

            assertEquals(contentString, actualContents, "변환된 프레임 내용이 원본 콘텐츠와 일치해야 함")
        }

    @Test
    fun `SubFrame-Content를 파싱 할 수 있다 (Last)`() =
        withEmbeddedChannel(sut) {
            // given
            val contentString = "This is content data"
            val contentBuf = Unpooled.copiedBuffer(contentString, CharsetUtil.UTF_8)
            val contentSubframe = DefaultLastStompContentSubframe(contentBuf)

            // when
            it.writeOutbound(contentSubframe)

            // then
            val actual = it.readOutbound<WebSocketFrame>()
            assertTrue(actual is ContinuationWebSocketFrame, "ContentSubframe은 ContinuationWebSocketFrame으로 변환되어야 함")
            assertTrue((actual as ContinuationWebSocketFrame).isFinalFragment, "LastContentSubframe은 final fragment여야 함")

            val actualContents = String(ByteBufUtil.getBytes(actual.content()), CharsetUtil.UTF_8)
            println(actualContents)

            assertEquals("${contentString}\u0000", actualContents, "변환된 프레임이 마지막이라면 null-octet을 포함한 값과 일치해야함")
        }
}
