FROM cgr.dev/chainguard/jdk as runtime

LABEL authors="Zineb Mabchour"

WORKDIR /app

COPY target/Taskmanagement2-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
