FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/gateway-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "app.jar"]
