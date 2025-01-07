FROM cgr.dev/chainguard/jdk as runtime

WORKDIR /app

COPY target/StorageManager-0.0.1-SNAPSHOT.jar StorageManager-0.0.1-SNAPSHOT.jar

EXPOSE 8090

CMD ["java", "-jar", "StorageManager-0.0.1-SNAPSHOT.jar"]
