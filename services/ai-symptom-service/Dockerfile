# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn install -N -B
COPY services/ai-symptom-service/pom.xml ./services/ai-symptom-service/
WORKDIR /app/services/ai-symptom-service
COPY services/ai-symptom-service/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/ai-symptom-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
