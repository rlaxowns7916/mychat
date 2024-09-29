package com.example.mychat.controller.rest.handler.room

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class RoomController {
    @GetMapping("/api/v1/rooms{roomId}")
    suspend fun getRoom(
        @PathVariable roomId: String,
    ) {
    }

    @GetMapping("/api/v1/rooms/{roomId}/participants")
    suspend fun getParticipants(
        @PathVariable roomId: String,
    ) {
    }
}
