FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/search-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8088
ENV SPRING_PROFILES_ACTIVE=""
ENTRYPOINT ["java", "-jar", "app.jar"]
