package com.example.websocketgateway.supports

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC

class NettyLogger private constructor(
    private val delegate: KLogger,
) {
    fun info(
        traceContext: TraceContext,
        message: () -> String,
    ) {
        withMDC(traceContext.next()) {
            delegate.info(message)
        }
    }

    fun error(
        traceContext: TraceContext,
        throwable: Throwable,
        message: () -> String,
    ) {
        withMDC(traceContext.next()) {
            delegate.error(throwable, message)
        }
    }

    private fun withMDC(
        traceContext: TraceContext,
        logging: () -> Unit,
    ) {
        try {
            MDC.put("traceId", traceContext.traceId)
            MDC.put("spanId", traceContext.spanId)

            logging()
        } finally {
            MDC.clear()
        }
    }

    companion object {
        fun getLogger(clazz: Class<*>): NettyLogger {
            return NettyLogger(KotlinLogging.logger(clazz.simpleName))
        }
    }
}
