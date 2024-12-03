# Use an OpenJDK 21 base image
FROM openjdk:21-jdk-slim as runtime

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/storage-service-0.0.1-SNAPSHOT.jar storage-service-0.0.1-SNAPSHOT.jar

# Expose the port your application runs on (default for Spring Boot is 8080)
EXPOSE 8080

# Specify the environment variables for S3 or Kafka if needed (optional)
ENV AWS_REGION=eu-north-1
ENV KAFKA_BROKER=localhost:9092

# Run the Spring Boot application
CMD ["java", "-jar", "storage-service-0.0.1-SNAPSHOT.jar"]
