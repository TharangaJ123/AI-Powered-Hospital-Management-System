# Use a multi-stage build for efficiency
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent pom.xml from the project root
COPY pom.xml .

# Copy the specific service pom.xml and source code
COPY services/ai-symptom-service/pom.xml services/ai-symptom-service/
COPY services/ai-symptom-service/src services/ai-symptom-service/src

# Build the service
RUN mvn -f services/ai-symptom-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/services/ai-symptom-service/target/*.jar ai-symptom-service.jar

# Expose port
EXPOSE 8087

ENTRYPOINT ["java", "-jar", "ai-symptom-service.jar"]
