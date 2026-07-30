# Stage 1: Build the Java application using Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy Maven setup files first to leverage Docker layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline || true

# Copy source code and build the package
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create lightweight runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]