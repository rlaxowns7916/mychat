package com.example.mychat.storage.redis.configs

import com.example.mychat.storage.redis.supports.EmbeddedRedisExtensions
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@Configuration
class EmbeddedRedisStorageConfiguration {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val port = 6379 // EmbeddedRedisExtensions.findAvailablePort()
    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun postConstruct() {
        redisServer =
            if (EmbeddedRedisExtensions.isArmMac()) {
                EmbeddedRedisExtensions.createEmbeddedRedis("arm-embedded-redis-server", port)
            } else {
                RedisServer(port)
            }
        redisServer.start()
        logger.info("[EmbeddedRedis][Storage][Start][Complete] (port:$port)")
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
        logger.info("[EmbeddedRedis][Stop]")
    }
}
