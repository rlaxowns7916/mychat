package com.example.websocketgateway.supports

import io.netty.util.AttributeKey
import java.util.UUID

data class TraceContext private constructor(
    val traceId: String,
    val spanId: String,
) {
    fun next(): TraceContext {
        return TraceContext(
            traceId = traceId,
            spanId = generateSpanId(),
        )
    }

    companion object {
        val TRACE_CONTEXT_ATTRIBUTE_KEY = AttributeKey.valueOf<TraceContext>(this::class.java.name)

        fun root(): TraceContext {
            return TraceContext(
                traceId = generateTraceId(),
                spanId = generateSpanId(),
            )
        }
    }
}

private fun generateTraceId(): String {
    val uuid = UUID.randomUUID()
    return String.format("%016x%016x", uuid.mostSignificantBits, uuid.leastSignificantBits)
}

private fun generateSpanId(): String {
    val uuid = UUID.randomUUID()
    return String.format("%016x", uuid.leastSignificantBits)
}
