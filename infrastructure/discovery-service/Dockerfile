# Use a multi-stage build for efficiency
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent pom.xml from the project root
COPY pom.xml .

# Copy the specific service pom.xml and source code
COPY infrastructure/discovery-service/pom.xml infrastructure/discovery-service/
COPY infrastructure/discovery-service/src infrastructure/discovery-service/src

# Build the service
RUN mvn -f infrastructure/discovery-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/infrastructure/discovery-service/target/*.jar discovery-service.jar

# Expose port
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "discovery-service.jar"]
