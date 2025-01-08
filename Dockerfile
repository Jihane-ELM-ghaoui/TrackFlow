FROM cgr.dev/chainguard/jdk as runtime

WORKDIR /app

COPY target/kpi-service-0.0.1-SNAPSHOT.jar kpi-service-0.0.1-SNAPSHOT.jar

EXPOSE 8010

CMD ["java", "-jar", "kpi-service-0.0.1-SNAPSHOT.jar"]
