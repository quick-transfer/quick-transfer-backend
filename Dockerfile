FROM maven:3.9.11-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp clean verify

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S quicktransfer && adduser -S quicktransfer -G quicktransfer
WORKDIR /app

COPY --from=builder --chown=quicktransfer:quicktransfer /workspace/target/*.jar app.jar

USER quicktransfer
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q -O /dev/null http://127.0.0.1:8080/api/actuator/health || exit 1

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
