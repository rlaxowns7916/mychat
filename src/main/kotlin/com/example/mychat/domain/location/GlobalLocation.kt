package com.example.mychat.domain.location

data class GlobalLocation(
    val identifier: String,
) {
    fun toChannel(): String {
        return "channel:$identifier"
    }
}
