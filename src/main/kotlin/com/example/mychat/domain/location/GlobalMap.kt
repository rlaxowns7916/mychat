package com.example.mychat.domain.location

import com.example.mychat.storage.redis.repository.RedisStringRepository
import com.example.mychat.storage.redis.repository.get
import com.example.mychat.storage.redis.repository.set
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

@Component
class GlobalMap(
    private val redisStringRepository: RedisStringRepository,
) {
    private lateinit var instanceLocationId: String
    private val globalMapLifeSpan: Duration = Duration.ofMinutes(10)

    fun init() {
        instanceLocationId = UUID.randomUUID().toString()
        logger.info { "[GlobalMap][Initialize] (instnaceId: $instanceLocationId)" }
    }

    fun get(): GlobalLocation {
        return GlobalLocation(instanceLocationId)
    }

    suspend fun get(userId: String): GlobalLocation? {
        return redisStringRepository.get<String>(userId)?.let {
            return GlobalLocation(it)
        }
    }

    suspend fun put(userId: String) {
        redisStringRepository.set(userId, instanceLocationId, globalMapLifeSpan)
    }

    suspend fun clear(userId: String): Boolean {
        return redisStringRepository.delete(userId)
    }

    suspend fun clearAll(userIds: List<String>): Boolean {
        return redisStringRepository.deleteAll(userIds)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
