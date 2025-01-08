FROM cgr.dev/chainguard/jdk as runtime

WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar user-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]
