plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
    id("org.flywaydb.flyway") version "11.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.1"))

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")

    implementation("com.discord4j:discord4j-core:3.3.0-RC1")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.postgresql:postgresql")

    implementation("org.projectlombok:lombok:1.18.28")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.15.2")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
    compileOnly("org.projectlombok:lombok:1.18.36")
}

tasks.test {
    useJUnitPlatform()
}