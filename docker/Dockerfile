# ---- Stage 1: Build ----
    FROM gradle:7.6-jdk17 AS builder

    WORKDIR /app

    COPY ../build.gradle.kts ../settings.gradle.kts ../gradlew ./
    COPY ../gradle ./gradle

    # Set executable permissions for the Gradle wrapper
    RUN chmod +x gradlew

    RUN ./gradlew dependencies --no-daemon

    # Copy the entire source code from the parent directory
    COPY ../ ./

    # Reset permissions on gradlew (just in case it is overwritten which has happened and I'm not crazy)
    RUN chmod +x gradlew

    RUN ./gradlew bootJar --no-daemon && \
        ls -l /app/build/libs

    #### ---- Stage 2: Runtime ---- ####
    FROM openjdk:17-jdk-slim
    
    WORKDIR /app

    # Install Timezone tools
    RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    rm -rf /var/lib/apt/lists/*

    # Copy the built JAR file from the builder stage
    COPY --from=builder /app/build/libs/*.jar app.jar

    RUN ls -l 
    
    # Expose the application port (Not now, but maybe later)
    # EXPOSE 8080
    
    # Define the log directory
    VOLUME /app/logs
    
    # Run the application
    ENTRYPOINT ["java", "-jar", "app.jar"]