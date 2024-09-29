package com.example.mychat.storage.redis

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.sendAndAwait
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class PubSubManager(
    private val redisTemplate: ReactiveStringRedisTemplate,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val subscriptions: ConcurrentHashMap<String, Subscription> = ConcurrentHashMap()

    suspend fun publish(
        channelName: String,
        message: String,
    ) {
        redisTemplate.sendAndAwait(channelName, message)
    }

    suspend fun subscribe(channelName: String): SharedFlow<String> {
        val subscription =
            subscriptions.computeIfAbsent(channelName) {
                val local = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = Int.MAX_VALUE)
                val remote =
                    redisTemplate.listenTo(ChannelTopic(channelName))
                        .asFlow()
                        .buffer(Int.MAX_VALUE)
                        .onEach { local.emit(it.message) }
                        .catch { logger.error("[PubsubManager][Remote][Subscription][Fail] (cause: ${it.message})") }
                        .launchIn(scope)

                local.transformWhile {
                    emit(it)
                    remote.isActive
                }

                Subscription(remote, local)
            }

        return subscription.local
    }

    suspend fun unsubscribe(channelName: String) {
        val subscription = subscriptions.remove(channelName)
        subscription?.remote?.cancel()
    }

    data class Subscription(
        val remote: Job,
        val local: SharedFlow<String>,
    )

    companion object {
        private val logger = LoggerFactory.getLogger(PubSubManager::class.java)
    }
}
