FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S quicktransfer && adduser -S quicktransfer -G quicktransfer

COPY --from=builder --chown=quicktransfer:quicktransfer /app/target/*.jar app.jar

USER quicktransfer

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q -O - http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
