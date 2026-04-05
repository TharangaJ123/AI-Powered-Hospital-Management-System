# Use an official Maven image to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the parent pom.xml
COPY pom.xml .

# Copy the service-specific pom.xml and source code
COPY services/telemedicine-service/pom.xml services/telemedicine-service/
COPY services/telemedicine-service/src services/telemedicine-service/src

# Build the service
RUN mvn -f services/telemedicine-service/pom.xml clean package -DskipTests

# Use an official OpenJDK image to run the service
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file from the previous stage
COPY --from=build /app/services/telemedicine-service/target/*.jar telemedicine-service.jar

# Expose the port the service runs on
EXPOSE 8086

# Define entrypoint
ENTRYPOINT ["java", "-jar", "telemedicine-service.jar"]
