FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
COPY .env .env

RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]