package com.example.mychat.controller.rest.handler.chat

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController {
    @PostMapping("/api/v1/messages")
    fun sendMessage() {
    }
}
