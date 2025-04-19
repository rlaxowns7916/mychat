package com.example.sessionmap.configuration

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Profile("dev", "live")
@Configuration
internal class RedisStorageConfiguration internal constructor(
    private val properties: RedisProperties,
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(properties.host, properties.port)
    }
}