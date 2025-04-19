dependencies {
    implementation("io.lettuce:lettuce-core:${project.property("lettuceVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("it.ozimov:embedded-redis:${project.property("embeddedRedisVersion")}")
}