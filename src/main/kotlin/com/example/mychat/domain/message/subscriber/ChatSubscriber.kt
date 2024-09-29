package com.example.mychat.domain.message.subscriber

import com.example.mychat.domain.location.GlobalMap
import com.example.mychat.domain.location.LocalMap
import com.example.mychat.domain.message.Message
import com.example.mychat.domain.message.MessageConverter
import com.example.mychat.domain.message.MessageType
import com.example.mychat.storage.redis.PubSubManager
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage

@Component
class ChatSubscriber(
    private val localMap: LocalMap,
    private val globalMap: GlobalMap,
    private val pubSubManager: PubSubManager,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @PostConstruct
    fun init() {
        val location = globalMap.get()
        logger.info { "[ChatSubscriber][Initialize] (channel: ${location.toChannel(MessageType.CHAT)}" }
        scope.launch { handle(location.toChannel(MessageType.CHAT)) }
    }

    suspend fun handle(channel: String) {
        pubSubManager.subscribe(channel).onEach {
            val inbound = MessageConverter.deserialize<Message.Chat>(it)
            val session = localMap.get(inbound.body.to) ?: return@onEach

            val outbound = TextMessage(it.toByteArray())
            runCatching {
                logger.info { "[ChatSubscriber][Subscribe] send ${inbound.body.payload} to ${inbound.body.to}(${session.id})" }
                session.sendMessage(outbound)
            }.onFailure {
                logger.error { "[ChatSubscriber][Send][Fail] (cause: $it)" }
            }
        }.launchIn(scope)
    }

    @PreDestroy
    fun destroy() {
        logger.info { "[ChatSubscriber][Destroy]" }
        scope.cancel()
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
