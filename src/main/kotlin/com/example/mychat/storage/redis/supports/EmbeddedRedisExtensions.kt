package com.example.mychat.storage.redis.supports

import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.file.Files

object EmbeddedRedisExtensions {
    fun createEmbeddedRedis(
        resourceName: String,
        port: Int,
    ): RedisServer {
        val redisBinaryFile = extractResourceAsFile(resourceName, port)
        val args =
            buildList {
                add(redisBinaryFile.absolutePath)
                add("--port")
                add(port.toString())
                add("--save")
                add("")
                add("--appendonly")
                add("no")
            }
        val redisServer =
            RedisServer().also { it.bindArgs(args) }

        return redisServer
    }

    fun isArmMac(): Boolean {
        return System.getProperty("os.arch") == "aarch64" && System.getProperty("os.name") == "Mac OS X"
    }

    private fun extractResourceAsFile(
        resourceName: String,
        port: Int,
    ): File {
        val resource = ClassPathResource(resourceName)
        require(resource.exists()) { "Resource not found: $resourceName" }

        val tempFile: File =
            Files.createTempFile("embedded-redis-$port", ".tmp").toFile()
                .apply {
                    setExecutable(true)
                    deleteOnExit()
                }
        resource.inputStream.use { inputStream ->
            FileCopyUtils.copy(inputStream, FileOutputStream(tempFile))
        }
        return tempFile
    }

    fun findAvailablePort(): Int {
        for (port in 30001..60000) {
            val process = executeGrepProcessCommand(port)
            if (!isRunning(process)) {
                return port
            }
        }

        throw IllegalArgumentException("Not Found Available port: 30001 ~ 60000")
    }

    fun executeGrepProcessCommand(port: Int): Process {
        val command = String.format("netstat -nat | grep LISTEN|grep %d", port)
        val shell = arrayOf("/bin/sh", "-c", command)
        return Runtime.getRuntime().exec(shell)
    }

    fun isRunning(process: Process): Boolean {
        var line: String?
        val pidInfo = StringBuilder()

        BufferedReader(InputStreamReader(process.inputStream)).use { input ->
            while (input.readLine().also { line = it } != null) {
                pidInfo.append(line)
            }
        }

        return pidInfo.toString().isNotEmpty()
    }

    private fun RedisServer.bindArgs(args: List<String>) {
        val prop = RedisServer::class.java.superclass.getDeclaredField("args")
        prop.isAccessible = true
        prop.set(this, args)
    }
}
