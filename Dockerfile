FROM openjdk:17-jdk-slim as build

WORKDIR /app

COPY target/notification-service-0.0.1-SNAPSHOT.jar notification-service-0.0.1-SNAPSHOT.jar

EXPOSE 8000

CMD ["java", "-jar", "notification-service-0.0.1-SNAPSHOT.jar"]