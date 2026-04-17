# Stage 1: Compilation and packaging using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copies the root POM to install shared dependencies
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn install -N -B

# Prepares the specific service directory for building
COPY services/user-management/pom.xml ./services/user-management/
WORKDIR /app/services/user-management

# Copies source code and builds the executable JAR while skipping tests for speed
COPY services/user-management/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Lean runtime environment
FROM eclipse-temurin:17-jre
WORKDIR /app
# Pulls only the compiled JAR from the build stage to keep the image small
COPY --from=build /app/services/user-management/target/*.jar app.jar
# Documents the port the service listens on
EXPOSE 8081
# Launches the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
