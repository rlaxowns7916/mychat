package com.example.mychat.controller.rest.handler

import com.example.mychat.controller.rest.supports.response.RestApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/health")
    fun healthCheck(): RestApiResponse<String> {
        return RestApiResponse.Success("OK")
    }
}
