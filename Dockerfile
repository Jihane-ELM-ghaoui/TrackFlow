# Use an OpenJDK base image
FROM openjdk:17-jdk-slim as build

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your local machine into the container
COPY target/eureka-0.0.1-SNAPSHOT.jar eureka-0.0.1-SNAPSHOT.jar

# Expose the port the app will run on
EXPOSE 8761

# Run the Spring Boot application
CMD ["java", "-jar", "eureka-0.0.1-SNAPSHOT.jar"]