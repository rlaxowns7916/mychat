package com.example.websocketgateway.supports

import io.netty.channel.ChannelHandler
import io.netty.channel.embedded.EmbeddedChannel

fun withEmbeddedChannel(
    vararg channelHandlers: ChannelHandler,
    block: (EmbeddedChannel) -> Unit,
) {
    val channel = EmbeddedChannel(*channelHandlers)
    channel.attr(TraceContext.TRACE_CONTEXT_ATTRIBUTE_KEY).set(TraceContext.root())

    try {
        block(channel)
    } finally {
        channel.close()
    }
}
