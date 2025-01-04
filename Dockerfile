# Use an OpenJDK 17 base image
FROM openjdk:17-jdk-slim as runtime

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/StorageManager-0.0.1-SNAPSHOT.jar StorageManager-0.0.1-SNAPSHOT.jar

# Expose the port your application runs on (default for Spring Boot is 8090)
EXPOSE 8090

# Run the Spring Boot application
CMD ["java", "-jar", "StorageManager-0.0.1-SNAPSHOT.jar"]

