# Stage 1: Compilation and packaging using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copies the root POM to install shared dependencies
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn install -N -B

# Prepares the specific infrastructure directory for building
COPY infrastructure/api-gateway/pom.xml ./infrastructure/api-gateway/
WORKDIR /app/infrastructure/api-gateway

# Copies source code and builds the executable JAR while skipping tests for speed
COPY infrastructure/api-gateway/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Lean runtime environment
FROM eclipse-temurin:17-jre
WORKDIR /app
# Pulls only the compiled JAR from the build stage to keep the image small
COPY --from=build /app/infrastructure/api-gateway/target/*.jar app.jar
# Documents the port the service listens on
EXPOSE 8099
# Launches the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
