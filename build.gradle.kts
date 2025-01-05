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
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.1"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")

    implementation("com.discord4j:discord4j-core:3.2.0")

    implementation("org.flywaydb:flyway-core")
    
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.15.2")

    compileOnly("org.projectlombok:lombok:1.18.36")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}