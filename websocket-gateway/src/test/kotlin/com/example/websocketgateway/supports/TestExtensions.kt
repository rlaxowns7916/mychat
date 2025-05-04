package com.example.websocketgateway.supports

import io.netty.channel.ChannelHandler
import io.netty.channel.embedded.EmbeddedChannel

fun withEmbeddedChannel(
    vararg channelHandlers: ChannelHandler,
    block: (EmbeddedChannel) -> Unit,
) {
    val channel = EmbeddedChannel(*channelHandlers)
    try {
        block(channel)
    } finally {
        channel.close()
    }
}
