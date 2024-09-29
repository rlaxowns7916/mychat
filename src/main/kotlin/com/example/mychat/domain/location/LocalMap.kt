package com.example.mychat.domain.location

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class LocalMap {
    private val hubsByUser: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap()
    private val hubsBySocket: ConcurrentHashMap<WebSocketSession, String> = ConcurrentHashMap()

    fun getAllUserIds(): List<String> {
        return hubsByUser.keys.toList()
    }

    fun set(
        userId: String,
        session: WebSocketSession,
    ) {
        hubsByUser[userId] = session
        hubsBySocket[session] = userId
    }

    fun clear(userId: String): String {
        val session = hubsByUser.remove(userId)
        if (session != null) {
            hubsBySocket.remove(session)
        }
        return userId
    }

    fun clear(session: WebSocketSession): String? {
        val userId = hubsBySocket.remove(session)
        if (userId != null) {
            hubsByUser.remove(userId)
        }
        return userId
    }
}
