# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy and install the parent POM
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn install -N -B

# Copy the discovery-service pom
COPY infrastructure/discovery-service/pom.xml ./infrastructure/discovery-service/
WORKDIR /app/infrastructure/discovery-service

# Copy source and build

# Copy source and build
COPY infrastructure/discovery-service/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/infrastructure/discovery-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

RUN apt-get update && apt-get install -y curl
