package com.example.sessionmap.configuration

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.concurrent.TimeUnit

object EmbeddedRedisExtensions {

    private val logger = KotlinLogging.logger {  }

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

        val tempFile: File = Files.createTempFile("embedded-redis-$port", ".tmp").toFile()
        tempFile.setExecutable(true)
        tempFile.deleteOnExit()

        FileOutputStream(tempFile).use { outputStream ->
            resource.inputStream.use { inputStream ->
                FileCopyUtils.copy(inputStream, outputStream)
            }
        }

        return tempFile
    }

    fun findAvailablePort(): Int {
        for (port in 30001..60000) {
            try {
                val process = executeGrepProcessCommand(port)
                val isPortUsed = isRunning(process)
                process.destroy() // 프로세스 명시적 종료

                if (!isPortUsed) {
                    return port
                }
            } catch (e: IOException) {
                continue
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
        return try {
            val pidInfo = StringBuilder()

            BufferedReader(InputStreamReader(process.inputStream)).use { input ->
                var line: String?
                while (input.readLine().also { line = it } != null) {
                    pidInfo.append(line)
                }
            }

            process.waitFor(1, TimeUnit.SECONDS)

            pidInfo.toString().isNotEmpty()
        } catch (e: Exception) {
            logger.error(e) { "[RedisServer][isRunning][CheckFail]" }
            process.destroy()
            false
        }
    }

    private fun RedisServer.bindArgs(args: List<String>) {
        runCatching {
            val prop = RedisServer::class.java.superclass.getDeclaredField("args")
            prop.isAccessible = true
            prop.set(this, args)
        }.onFailure {
            logger.error(it){ "[RedisServer][Bind][Fail]" }
        }
    }
}