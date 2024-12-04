FROM openjdk:17-jdk-slim

LABEL authors="Zineb Mabchour"

WORKDIR /app

COPY target/Taskmanagement2-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
