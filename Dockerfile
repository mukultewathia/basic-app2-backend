# Use Java 17 base image (can change to 21 if needed)
# FROM eclipse-temurin:17-jdk

# # Set working directory inside the container
# WORKDIR /app

# # Copy the Spring Boot JAR (make sure it's built)
# COPY target/*.jar app.jar

# # Run the Spring Boot app
# ENTRYPOINT ["java", "-jar", "app.jar"]


# syntax=docker/dockerfile:1                # modern BuildKit features

# ──────────────── 1. Build stage ────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder     
WORKDIR /app

# Copy everything first (so --layers=true caches Maven deps)
COPY . .

# If you use the Maven wrapper, keep it executable
RUN chmod +x mvnw

# Build the fat-jar; skip tests for faster CI builds
RUN ./mvnw -B package -DskipTests

# ──────────────── 2. Run stage ────────────────
FROM eclipse-temurin:17-jre-alpine

# Render always injects a PORT environment variable.
# Expose it for local docker runs; Spring gets the same via -Dserver.port=$PORT
ENV PORT 8080
EXPOSE ${PORT}

WORKDIR /app

# Copy the packaged jar from the builder stage
# target/*.jar matches <artifactId>-<version>.jar regardless of snapshot/status
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# Minimal JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Start the app, binding to Render’s assigned port
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
