package com.example.websocketgateway.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.stomp.StompSubframeAggregator
import io.netty.handler.codec.stomp.StompSubframeDecoder
import io.netty.handler.codec.stomp.StompSubframeEncoder

class StompProtocolHandler(
    private val stompMessageHandler: StompMessageHandler,
) : ChannelInboundHandlerAdapter() {
    private val logger = KotlinLogging.logger {}

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.info { "[StompProtocolHandler][Active] (client: ${ctx.channel().remoteAddress()})" }
    }

    override fun userEventTriggered(
        ctx: ChannelHandlerContext,
        evt: Any,
    ) {
        if (evt is WebSocketServerProtocolHandler.HandshakeComplete) {
            val channel = ctx.channel()
            val remoteAddress = ctx.channel().remoteAddress()
            val requestUri = evt.requestUri()
            val selectedSubProtocol = evt.selectedSubprotocol()

            logger.info {
                """
                [StompProtocolHandler][HandshakeComplete]( 
                    - client: $remoteAddress, 
                    - requestUri: $requestUri, 
                    - selectedSubProtocol: $selectedSubProtocol
                )       
                """.trimIndent()
            }

            val stompVersion = StompVersion.fromSubProtocol(selectedSubProtocol)
            channel.attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).set(stompVersion)

            ctx.pipeline()
                /**
                 *  WebSocketFrameAggregator: WebSocket 프레임 조합기
                 * - WebSocket 프로토콜은 대용량 메시지를 여러 프레임으로 분할할 수 있음
                 * - WebSocketServerProtocolHandler가 추가한 기본 디코더는 분할된 프레임을 자동으로 조합하지 않음
                 * - 분할을 인식하는 방법: 첫 프레임(FIN=0, opcode=0x1/0x2), 중간/마지막 프레임(opcode=0x0)의 헤더 플래그로 구분
                 * - 프레임 집계가 없으면 STOMP 디코더는 불완전한 메시지를 받아 디코딩 실패 가능성 있음
                 */
                .addLast(WebSocketFrameAggregator(65536))
                /**
                 * StompSubframeEncoder: STOMP 프레임 인코더
                 * - 아웃바운드(서버→클라이언트) 방향으로 STOMP 프레임을 인코딩
                 * - 인코더는 아웃바운드 흐름(파이프라인 뒤→앞)에서 작동하므로 디코더보다 앞에 위치
                 * - 이렇게 해야 writeAndFlush()한 메시지가 인코더를 통과함
                 */
                .addLast(StompSubframeEncoder())
                /**
                 * StompSubframeDecoder: STOMP 프레임 디코더
                 * - 인바운드(클라이언트→서버) 방향으로 WebSocket 메시지를 STOMP 프레임으로 변환
                 * - WebSocketFrameAggregator가 조합한 완전한 WebSocket 메시지를 입력으로 받음
                 */
                .addLast(StompSubframeDecoder())
                /**
                 * StompSubframeAggregator: STOMP 서브프레임 조합기
                 * - STOMP 프로토콜도 대형 메시지를 여러 서브프레임으로 나눌 수 있음
                 * - 디코딩된 서브프레임들을 완전한 STOMP 메시지로 조합
                 * - 이 과정이 없으면 stompMessageHandler가 불완전한 STOMP 메시지를 받을 수 있음
                 */
                .addLast(StompSubframeAggregator(65536))
                .addLast(stompMessageHandler)
                .remove(this)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.info { "[StompProtocolHandler][InActive] (client: ${ctx.channel().remoteAddress()})" }
    }

    override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        logger.error(cause) { "[StompProtocolHandler][ErrorCaught]" }
        ctx.close()
    }
}
