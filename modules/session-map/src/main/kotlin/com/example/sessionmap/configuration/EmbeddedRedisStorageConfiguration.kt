package com.example.sessionmap.configuration

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import redis.embedded.RedisServer

@Profile("local")
@Configuration
internal class EmbeddedRedisStorageConfiguration {
    private val port = EmbeddedRedisExtensions.findAvailablePort()
    private lateinit var redisServer: RedisServer

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        logger.info { "[EmbeddedRedis][Storage][Connection] (host: 127.0.0.1, port:$port)" }
        return LettuceConnectionFactory("127.0.0.1", port)
    }

    @PostConstruct
    fun postConstruct() {
        redisServer =
            if (EmbeddedRedisExtensions.isArmMac()) {
                EmbeddedRedisExtensions.createEmbeddedRedis("arm-embedded-redis-server", port)
            } else {
                RedisServer(port)
            }
        redisServer.start()
        logger.info { "[EmbeddedRedis][Storage][Start][Complete] (port:$port)" }
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
        logger.info("[EmbeddedRedis][Stop]")
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
