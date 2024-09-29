package com.example.mychat.controller.websocket.configs

import com.example.mychat.controller.websocket.handler.WebSocketController
import com.example.mychat.controller.websocket.handler.WebSocketHandshakeInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val webSocketHandler: WebSocketController,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketHandler, "/websocket")
            .addInterceptors(WebSocketHandshakeInterceptor())
            .setAllowedOrigins("*")
    }
}
