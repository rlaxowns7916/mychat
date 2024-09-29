package com.example.mychat.storage.redis.repository

import com.example.mychat.storage.redis.supports.RedisRepositoryObjectMapperSupports
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Component
class RedisStringRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    suspend fun getStringValue(key: String): String? {
        return redisTemplate.opsForValue().getAndAwait(key)
    }

    suspend fun setStringValue(
        key: String,
        value: String,
        lifeSpan: Duration?,
    ): Boolean {
        return if (lifeSpan == null) {
            redisTemplate.opsForValue().setAndAwait(key, value)
        } else {
            redisTemplate.opsForValue().setAndAwait(key, value, lifeSpan)
        }
    }

    suspend fun delete(key: String): Boolean {
        return redisTemplate.deleteAndAwait(key) != 0L
    }

    suspend fun deleteAll(keys: List<String>): Boolean {
        return redisTemplate.unlink(keys.toFlux())
            .awaitSingle() == keys.size.toLong()
    }
}

suspend inline fun <reified T> RedisStringRepository.get(key: String): T? {
    val value = getStringValue(key) ?: return null
    return RedisRepositoryObjectMapperSupports.objectMapper.readValue<T>(value)
}

suspend inline fun <reified T> RedisStringRepository.set(
    key: String,
    value: T,
    lifeSpan: Duration? = null,
) {
    setStringValue(key, RedisRepositoryObjectMapperSupports.objectMapper.writeValueAsString(value), lifeSpan)
}
