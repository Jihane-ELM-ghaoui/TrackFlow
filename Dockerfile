FROM cgr.dev/chainguard/jdk as runtime

WORKDIR /app

COPY target/task-service-0.0.1-SNAPSHOT.jar task-service-0.0.1-SNAPSHOT.jar

EXPOSE 8095

CMD ["java", "-jar", "task-service-0.0.1-SNAPSHOT.jar"]
