# Use a multi-stage build for efficiency
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent pom.xml from the project root
COPY pom.xml .

# Copy the specific service pom.xml and source code
COPY services/doctor-management/pom.xml services/doctor-management/
COPY services/doctor-management/src services/doctor-management/src

# Build the service (skip tests for faster deployment)
RUN mvn -f services/doctor-management/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/services/doctor-management/target/*.jar doctor-management.jar

# Expose port
EXPOSE 8085

# Health check (optional but recommended for K8s)
# HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "doctor-management.jar"]
