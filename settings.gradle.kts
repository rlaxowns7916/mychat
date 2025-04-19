pluginManagement {
    plugins {
        val kotlinVersion = "2.0.0-RC1"
        val springBootVersion = "3.3.3"
        val springDependencyManagementVersion = "1.1.4"
        val ktlintVersion = "12.1.0"

        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "mychat"

include(
    ":core-api",
    ":websocket-gateway",
    ":modules:session-map",
    ":storage:rdb"
)
