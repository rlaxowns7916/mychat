package com.example.websocketgateway.websocket

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.timeout.IdleStateHandler
import java.util.concurrent.TimeUnit

class WebSocketServerPipeLineInitializer(
    private val stompMessageHandler: StompMessageHandler,
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val pipeLine = ch.pipeline()
        /**
         * HttpServerCodec: HTTP 요청/응답 코덱
         * - HTTP 요청을 디코딩하고 HTTP 응답을 인코딩하는 복합 코덱
         * - WebSocket 연결은 HTTP 업그레이드로 시작하므로 필수적인 첫 번째
         * - WebSocketUpgrade 이후 pipeLine에서 제거된다.
         *   @see io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
         */
        pipeLine.addLast(HttpServerCodec())
        /**
         * HttpObjectAggregator: HTTP 메시지 조각 조합기
         * - 분할된 HTTP 메시지를 완전한 FullHttpRequest/FullHttpResponse로 조합
         * - WebSocket 핸드셰이크 처리를 위해 완전한 HTTP 요청이 필요함
         * - MAX_CONTENT_LENGTH(65536)는 HTTP 메시지 최대 크기 제한
         * - WebSocketUpgrade 이후 pipeLine에서 제거된다.
         *   @see io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
         */
        pipeLine.addLast(HttpObjectAggregator(MAX_CONTENT_LENGTH))
        /**
         * WebSocketServerCompressionHandler: WebSocket 압축 처리
         * - WebSocket 통신 시 데이터 압축/해제 기능 제공
         * - 네트워크 대역폭 절약을 위한 선택적 기능
         */
        pipeLine.addLast(WebSocketServerCompressionHandler())
        /**
         * WebSocketServerProtocolHandler: WebSocket 핸드셰이크 처리기
         * - HTTP를 WebSocket으로 업그레이드하는 핸드셰이크 처리
         * - 핸드셰이크 성공 시 WebSocketFrameDecoder/Encoder를 파이프라인에 자동 추가
         */
        pipeLine.addLast(WebSocketServerProtocolHandler(WEBSOCKET_PATH, StompVersion.getSupportedSubProtocols(), true))
        /**
         * IdleStateHandler: 연결 유휴 상태 관리
         * - 채널 활동 감시하여 타임아웃 이벤트 생성
         * - 매개변수: 읽기 타임아웃(60초), 쓰기 타임아웃(30초), 전체 타임아웃(사용안함)
         */
        pipeLine.addLast(IdleStateHandler(60, 30, 0, TimeUnit.SECONDS))
        pipeLine.addLast(StompProtocolHandler(stompMessageHandler))
    }

    companion object {
        private const val WEBSOCKET_PATH = "/chat"
        private const val MAX_CONTENT_LENGTH = 65536
    }
}
