# Use a multi-stage build for efficiency
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent pom.xml from the project root
COPY pom.xml .

# Copy the specific service pom.xml and source code
COPY services/user-management/pom.xml services/user-management/
COPY services/user-management/src services/user-management/src

# Build the service
RUN mvn -f services/user-management/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/services/user-management/target/*.jar user-management.jar

# Expose port
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "user-management.jar"]
