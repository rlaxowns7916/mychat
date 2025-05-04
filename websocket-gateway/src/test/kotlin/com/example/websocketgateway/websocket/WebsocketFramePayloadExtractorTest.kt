package com.example.websocketgateway.websocket

import com.example.websocketgateway.supports.withEmbeddedChannel
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WebsocketFramePayloadExtractorTest {
    private val sut = WebsocketFramePayloadExtractor()

    @Test
    fun `TextWebSocketFrame의 content가 추출되고 ReferenceCount가 유지되는지 검증`() =
        withEmbeddedChannel(sut) {
            val testContent = "Test WebSocket Content"
            val contentBuf = Unpooled.copiedBuffer(testContent.toByteArray())
            val webSocketFrame = TextWebSocketFrame(contentBuf)

            // when
            val result = it.writeInbound(webSocketFrame)

            // then
            assertTrue(result, "채널 쓰기 성공해야 함")
            val extractedBuf = it.readInbound<ByteBuf>()

            assertNotNull(extractedBuf, "추출된 ByteBuf는 null이 아니어야 함")

            val extractedContent = ByteArray(extractedBuf.readableBytes())
            extractedBuf.getBytes(extractedBuf.readerIndex(), extractedContent)
            assertEquals(testContent, String(extractedContent), "추출된 내용이 원본과 일치해야 함")

            assertEquals(1, extractedBuf.refCnt(), "ReferenceCount가 유지되어야함")

            extractedBuf.release()
        }

    @Test
    fun `BinaryWebSocketFrame 처리 검증`() =
        withEmbeddedChannel(sut) {
            // given
            val binaryData = ByteArray(10) { it.toByte() }
            val contentBuf = Unpooled.copiedBuffer(binaryData)
            val webSocketFrame = BinaryWebSocketFrame(contentBuf)

            // when
            it.writeInbound(webSocketFrame)

            // then
            val extractedBuf = it.readInbound<ByteBuf>()
            assertNotNull(extractedBuf, "추출된 ByteBuf는 null이 아니어야 함")

            val extractedBinary = ByteArray(extractedBuf.readableBytes())
            extractedBuf.getBytes(extractedBuf.readerIndex(), extractedBinary)
            assertArrayEquals(binaryData, extractedBinary, "추출된 바이너리 데이터가 원본과 일치해야 함")

            extractedBuf.release()
        }

    @Test
    fun `빈 WebSocketFrame 처리 검증`() =
        withEmbeddedChannel(sut) {
            // given
            val emptyBuf = Unpooled.EMPTY_BUFFER
            val webSocketFrame = TextWebSocketFrame(emptyBuf)

            // when
            it.writeInbound(webSocketFrame)

            // then
            val extractedBuf = it.readInbound<ByteBuf>()
            assertNotNull(extractedBuf, "빈 버퍼도 추출되어야 함")
            assertEquals(0, extractedBuf.readableBytes(), "읽을 수 있는 바이트가 0이어야 함")

            // 리소스 해제
            extractedBuf.release()
        }
}
