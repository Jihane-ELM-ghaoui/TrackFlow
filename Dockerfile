# Use an OpenJDK base image
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your local machine into the container
COPY target/task-service-0.0.1-SNAPSHOT.jar task-service-0.0.1-SNAPSHOT.jar

# Expose the port the app will run on
EXPOSE 8095

# Run the Spring Boot application
CMD ["java", "-jar", "task-service-0.0.1-SNAPSHOT.jar"]