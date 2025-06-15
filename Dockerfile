# Use Java 17 base image (can change to 21 if needed)
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR (make sure it's built)
COPY target/*.jar app.jar

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
