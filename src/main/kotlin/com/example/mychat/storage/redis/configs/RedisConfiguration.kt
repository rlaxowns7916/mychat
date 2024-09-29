package com.example.mychat.storage.redis.configs

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration(
    private val redisProperties: RedisProperties,
) {
    @Bean
    fun reactiveRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any> {
        val context = RedisSerializationContext.newSerializationContext<String, Any>(StringRedisSerializer()).build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }
}
