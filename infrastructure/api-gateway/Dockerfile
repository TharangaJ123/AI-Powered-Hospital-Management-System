# Use a multi-stage build for efficiency
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent pom.xml from the project root
COPY pom.xml .

# Copy the specific service pom.xml and source code
COPY infrastructure/api-gateway/pom.xml infrastructure/api-gateway/
COPY infrastructure/api-gateway/src infrastructure/api-gateway/src

# Build the service
RUN mvn -f infrastructure/api-gateway/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/infrastructure/api-gateway/target/*.jar api-gateway.jar

# Expose port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
