FROM openjdk:17-jdk-slim as build

WORKDIR /app

COPY target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service-0.0.1-SNAPSHOT.jar

EXPOSE 8888

CMD ["java", "-jar", "gateway-service-0.0.1-SNAPSHOT.jar"]
