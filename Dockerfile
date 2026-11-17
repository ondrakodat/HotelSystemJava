FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY domain/pom.xml domain/pom.xml
COPY application/pom.xml application/pom.xml
COPY infrastructure/pom.xml infrastructure/pom.xml
COPY api/pom.xml api/pom.xml

RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY domain domain
COPY application application
COPY infrastructure infrastructure
COPY api api

RUN ./mvnw -B -pl api -am package -DskipTests

FROM eclipse-temurin:25-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/api/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=10 \
    CMD curl -fsS http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]