# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy and install the parent POM (though not used as parent, useful for context consistency)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn install -N -B

# Copy the service pom
COPY services/user-management/pom.xml ./services/user-management/
WORKDIR /app/services/user-management

# Copy source and build

# Copy source and build
COPY services/user-management/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/user-management/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
