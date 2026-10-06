# syntax=docker/dockerfile:1.7

# ---------------------------------------------------------
# Stage 1: Build the Spring Boot application using Gradle
# ---------------------------------------------------------
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

# Copy Gradle wrapper and configuration files
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Pre-cache dependencies using BuildKit cache mount
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew && \
    ./gradlew dependencies --no-daemon

# Copy source code and package executable bootJar
COPY src ./src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon -x test

# ---------------------------------------------------------
# Stage 2: Extract Spring Boot layers and fetch Datadog tracer
# ---------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS extractor

WORKDIR /extract

# Install curl for downloading tracer
RUN apk add --no-cache curl

# Configurable Datadog Java Tracer version (defaults to latest)
ARG DD_JAVA_AGENT_VERSION=latest
RUN if [ "$DD_JAVA_AGENT_VERSION" = "latest" ]; then \
        curl -fsSL https://dtdg.co/latest-java-tracer -o /extract/dd-java-agent.jar; \
    else \
        curl -fsSL https://github.com/DataDog/dd-trace-java/releases/download/v${DD_JAVA_AGENT_VERSION}/dd-java-agent.jar -o /extract/dd-java-agent.jar; \
    fi

# Copy JAR from builder and unpack layers into /extract/layers
COPY --from=builder /build/build/libs/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination layers

# ---------------------------------------------------------
# Stage 3: Minimal, Hardened Runtime Image
# ---------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS runtime

# Metadata labels
LABEL org.opencontainers.image.title="payment-service" \
      org.opencontainers.image.description="Spring Cloud Stream payment processing service"

WORKDIR /app

# Create dedicated non-root user and group
RUN addgroup -S -g 1000 events && \
    adduser -S -u 1000 -G events -h /app events

# Copy Datadog agent
COPY --from=extractor /extract/dd-java-agent.jar /app/dd-java-agent.jar

# Copy Spring Boot layers in order of change frequency (least to most)
COPY --from=extractor /extract/layers/dependencies/ ./
COPY --from=extractor /extract/layers/spring-boot-loader/ ./
COPY --from=extractor /extract/layers/snapshot-dependencies/ ./
COPY --from=extractor /extract/layers/application/ ./

USER events

# Configure container-aware JVM ergonomics and memory bounds
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher \"$@\"", "--"]