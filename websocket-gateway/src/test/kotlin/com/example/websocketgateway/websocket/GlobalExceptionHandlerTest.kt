package com.example.websocketgateway.websocket

import com.example.websocketgateway.domain.exception.DomainErrorType
import com.example.websocketgateway.domain.exception.DomainException
import com.example.websocketgateway.supports.withEmbeddedChannel
import io.netty.handler.codec.stomp.StompCommand
import io.netty.handler.codec.stomp.StompFrame
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {
    private val sut = GlobalExceptionHandler()

    @Test
    fun `예외가_발생하지않앗다면_명시적으로_close()하지_않는다`() =
        withEmbeddedChannel(sut) {
            it.writeInbound("Test message")
            assertTrue(it.isActive) { "예외가 없을 때 채널이 열려있어야 한다" }
        }

    @Test
    fun `DomainException을 던지면 해당 errorType으로 응답하고 채널을 닫는다"`() =
        withEmbeddedChannel(sut) {
            // given
            val errorType = DomainErrorType.INVALID_AUTH_KEY
            val domainException = DomainException(errorType)
            it.attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).set(StompVersion.VERSION1_2)

            // when
            it.pipeline().fireExceptionCaught(domainException)

            // then
            val responseFrame = it.readOutbound<StompFrame>()
            assertFalse(it.isActive) { "DomainException 발생 후 채널이 닫혀야 한다" }
            assertEquals(responseFrame.command(), StompCommand.ERROR)
            assertTrue(responseFrame.content().toString(Charsets.UTF_8).contains(errorType.message))
        }

    @Test
    fun `일반 예외를 던지면 INTERNAL_SERVER_ERROR로 응답하고 채널을 닫는다"`() =
        withEmbeddedChannel(sut) {
            // given
            val generalException = RuntimeException("일반 오류")
            val expectedErrorType = DomainErrorType.INTERNAL_SERVER_ERROR
            it.attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).set(StompVersion.VERSION1_2)

            // when
            it.pipeline().fireExceptionCaught(generalException)

            // then
            val responseFrame = it.readOutbound<StompFrame>()
            assertFalse(it.isActive) { "일반 예외 발생 후 채널이 닫혀야 한다" }
            assertEquals(responseFrame.command(), StompCommand.ERROR)
            assertTrue(responseFrame.content().toString(Charsets.UTF_8).contains(expectedErrorType.message))
        }
}
