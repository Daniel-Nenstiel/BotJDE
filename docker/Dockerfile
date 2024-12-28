# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Install wget and unzip (required for downloading and extracting Gradle)
RUN apt-get update && apt-get install -y wget unzip

# Download and install Gradle
ENV GRADLE_VERSION=7.5
RUN wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" && \
    unzip -q "gradle-${GRADLE_VERSION}-bin.zip" -d /opt && \
    ln -s "/opt/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle && \
    rm "gradle-${GRADLE_VERSION}-bin.zip"

# Set environment variable for Gradle
ENV PATH=$PATH:/opt/gradle-${GRADLE_VERSION}/bin