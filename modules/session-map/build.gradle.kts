dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("it.ozimov:embedded-redis:${project.property("embeddedRedisVersion")}")
}