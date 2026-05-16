FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/order-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8084
ENV SPRING_PROFILES_ACTIVE=""
ENTRYPOINT ["java", "-jar", "app.jar"]
