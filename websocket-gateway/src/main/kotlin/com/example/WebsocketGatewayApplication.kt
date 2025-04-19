package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebsocketGatewayApplication

fun main(args: Array<String>) {
    runApplication<WebsocketGatewayApplication>(*args)
}
