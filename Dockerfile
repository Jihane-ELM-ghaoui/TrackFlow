FROM cgr.dev/chainguard/jdk as runtime

WORKDIR /app

COPY target/eureka-0.0.1-SNAPSHOT.jar eureka-0.0.1-SNAPSHOT.jar

EXPOSE 8761

CMD ["java", "-jar", "eureka-0.0.1-SNAPSHOT.jar"]
