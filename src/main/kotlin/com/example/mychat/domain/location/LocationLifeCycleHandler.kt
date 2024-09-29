package com.example.mychat.domain.location

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class LocationLifeCycleHandler(
    private val localMap: LocalMap,
    private val globalMap: GlobalMap,
) {
    private val lock = Mutex()

    @PostConstruct
    fun onInit() {
        globalMap.init()
    }

    suspend fun onConnect(
        userId: String,
        session: WebSocketSession,
    ) {
        lock.withLock {
            logger.info { "[LocationLifeCycleHandler][onConnect] (userId: $userId, session: ${session.id})" }
            globalMap.put(userId)
            localMap.set(userId, session)
        }
    }

    suspend fun onDisConnect(session: WebSocketSession) {
        lock.withLock {
            logger.info { "[LocationLifeCycleHandler][onDisConnect] (session: ${session.id})" }
            val userId = localMap.clear(session)
            if (userId != null) {
                globalMap.clear(userId)
            }
        }
    }

    @PreDestroy
    fun onDestroy() {
        runBlocking {
            lock.withLock {
                val userIds = localMap.getAllUserIds()
                globalMap.clearAll(userIds)
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
