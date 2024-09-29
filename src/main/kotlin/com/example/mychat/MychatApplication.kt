package com.example.mychat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MychatApplication

fun main(args: Array<String>) {
    runApplication<MychatApplication>(*args)
}
