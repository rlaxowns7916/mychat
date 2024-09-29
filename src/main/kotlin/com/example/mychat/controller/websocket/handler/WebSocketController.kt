package com.example.mychat.controller.websocket.handler

import com.example.mychat.controller.websocket.handler.WebSocketHandshakeInterceptor.Companion.USER_IDENTIFIER_HEADER_KEY
import com.example.mychat.domain.location.LocationLifeCycleHandler
import com.example.mychat.domain.message.MessageConverter
import com.example.mychat.domain.router.MessageRouter
import com.example.mychat.supports.error.ErrorType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSocketController(
    private val messageRouter: MessageRouter,
    private val locationLifeCycleHandler: LocationLifeCycleHandler,
) : TextWebSocketHandler() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun afterConnectionEstablished(session: WebSocketSession) {
        scope.launch {
            val userId = session.attributes[USER_IDENTIFIER_HEADER_KEY] as? String
            if (userId == null) {
                session.close(CloseStatus(ErrorType.USER_NOT_FOUND.code, ErrorType.USER_NOT_FOUND.externalMessage))
                return@launch
            }
            logger.info { "[WebSocketController][ConnectionEstablished] (userId: $userId, session: ${session.id})" }
            locationLifeCycleHandler.onConnect(userId, session)
        }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        scope.launch {
            val bytes = message.asBytes()
            print(String(bytes))
            val header = MessageConverter.deserializeHeader(bytes)
            messageRouter.route(header, bytes)
        }
    }

    override fun handleTransportError(
        session: WebSocketSession,
        exception: Throwable,
    ) {
        scope.launch {
            if (session.isOpen) {
                session.close()
            }
            val isClosed = session.isOpen.not()
            locationLifeCycleHandler.onDisConnect(session)
            logger.warn {
                "[WebSocketController][handleTransportError] (session: ${session.id}, exception: $exception, closed: $isClosed)"
            }
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        closeStatus: CloseStatus,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            locationLifeCycleHandler.onDisConnect(session)
        }
    }

    override fun supportsPartialMessages(): Boolean {
        return false
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
