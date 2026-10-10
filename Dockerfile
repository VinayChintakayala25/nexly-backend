
# Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

COPY src src

RUN mvn -DskipTests clean package

# Run the application
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=render

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
