dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.asyncer:r2dbc-mysql:${project.property("r2dbcMysqlVersion")}")
}
