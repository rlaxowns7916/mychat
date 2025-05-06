package com.example.websocketgateway.websocket

import com.example.websocketgateway.supports.NettyLogger
import com.example.websocketgateway.supports.TraceContext
import com.example.websocketgateway.websocket.command.StompCommandHandlerFactory
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.stomp.StompSubframeAggregator
import io.netty.handler.codec.stomp.StompSubframeDecoder

class StompProtocolHandler(
    private val commandHandlerFactory: StompCommandHandlerFactory,
) : ChannelInboundHandlerAdapter() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        val traceContext = TraceContext.root().also {
            ctx.channel().attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).set(it)
        }
        logger.info(traceContext) { "[StompProtocolHandler][Active] (client: ${ctx.channel().remoteAddress()})" }
    }

    override fun userEventTriggered(
        ctx: ChannelHandlerContext,
        evt: Any,
    ) {
        if (evt is WebSocketServerProtocolHandler.HandshakeComplete) {
            val channel = ctx.channel()
            val traceContext =ctx.channel().attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).get()
            val remoteAddress = ctx.channel().remoteAddress()
            val requestUri = evt.requestUri()
            val selectedSubProtocol = evt.selectedSubprotocol()

            logger.info(traceContext) {
                """
                [StompProtocolHandler][WebSocketHandshakeComplete]( 
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
                 * StompSubFrameToWebsocketFrameEncoder
                 * - StompSubFrame -> WebSocketFrame
                 * - 아래 있는 StompSubframeEncoder는 StompSubFrame -> ByteBuf
                 */
                .addLast(StompSubFrameToWebsocketFrameEncoder())
                /**
                 * WebsocketFramePayloadExtractor
                 * - WebSocketFrame -> Payload
                 * - WebSocketFrame의 payload를 추출
                 * - Stomp는 WebSocketFrame의 payload를 사용하기 떄문
                 * -  WebSocketFrameAggregator가 조합한 WebSocket 메시지를 입력으로 받음
                 */
                .addLast(WebsocketFramePayloadExtractor())
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
                .addLast(StompMessageHandler(commandHandlerFactory))
                .addLast(GlobalExceptionHandler())
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val traceContext = ctx.channel().attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).get()
        logger.info(traceContext) { "[StompProtocolHandler][InActive] (client: ${ctx.channel().remoteAddress()})" }
    }

    companion object {
        private val logger = NettyLogger.getLogger(StompProtocolHandler::class.java)
    }
}
