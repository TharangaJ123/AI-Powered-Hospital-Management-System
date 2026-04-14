# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy and install the parent POM
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn install -N -B

# Copy the api-gateway pom
COPY infrastructure/api-gateway/pom.xml ./infrastructure/api-gateway/
WORKDIR /app/infrastructure/api-gateway

# Copy source and build

# Copy source and build
COPY infrastructure/api-gateway/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/infrastructure/api-gateway/target/*.jar app.jar
EXPOSE 8099
ENTRYPOINT ["java", "-jar", "app.jar"]
