dependencies {
    implementation(project(":storage:rdb"))
    implementation(project(":storage:chat"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
