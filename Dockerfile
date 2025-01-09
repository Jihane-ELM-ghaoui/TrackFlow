FROM openjdk:17-jdk-slim as build

WORKDIR /app

COPY target/chat-service-0.0.1-SNAPSHOT.jar chat-service-0.0.1-SNAPSHOT.jar

EXPOSE 8001

CMD ["java", "-jar", "chat-service-0.0.1-SNAPSHOT.jar"]