package com.example.mychat.controller.websocket.handler

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

class WebSocketHandshakeInterceptor : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        val userId = request.headers.getFirst(USER_IDENTIFIER_HEADER_KEY)
        if (userId != null) {
            attributes[USER_IDENTIFIER_HEADER_KEY] = userId
        }
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {}

    companion object {
        const val USER_IDENTIFIER_HEADER_KEY = "X_USER_IDENTIFIER"
    }
}
