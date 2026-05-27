FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
COPY xpring/build.gradle xpring/build.gradle
COPY xpring/src xpring/src
COPY src src

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew shadowJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/fitory.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
