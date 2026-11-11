FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S ghostlead && adduser -S ghostlead -G ghostlead
WORKDIR /app
COPY --from=build /app/target/ghostlead-api-0.0.1-SNAPSHOT.jar app.jar
USER ghostlead
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
