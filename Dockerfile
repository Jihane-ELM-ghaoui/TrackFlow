FROM openjdk:17-jdk-slim as build

WORKDIR /app


COPY .env /app/.env

COPY target/user-service-0.0.1-SNAPSHOT.jar user-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]
