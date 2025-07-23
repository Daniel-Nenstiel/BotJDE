tasks.withType<Checkstyle> {
    configFile = file("config/checkstyle/checkstyle.xml")
    configProperties = configProperties.orEmpty() + mapOf(
        "checkstyle.suppressions.file" to when (name) {
            "checkstyleTest" -> file("config/checkstyle/test-suppressions.xml").absolutePath
            else -> file("config/checkstyle/main-suppressions.xml").absolutePath
        }
    )
}
plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
    id("org.flywaydb.flyway") version "11.1.0"
    id("checkstyle")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.discord4j:discord4j-core:3.3.0-RC1")

    // Spring Dependencies
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.1"))
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")

    implementation("org.projectlombok:lombok:1.18.28")

    // Database/Flyway config
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.postgresql:postgresql")

    //Google sheets config
    implementation("com.google.api-client:google-api-client:1.23.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev493-1.23.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("com.h2database:h2")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
    compileOnly("org.projectlombok:lombok:1.18.36")
}

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform {
        includeTags("integration")
    }
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    shouldRunAfter(tasks.test)
}