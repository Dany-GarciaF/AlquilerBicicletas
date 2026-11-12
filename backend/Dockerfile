FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true
COPY src src
RUN ./gradlew --no-daemon bootJar -x test \
    && cp build/libs/*[!n].jar app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --no-create-home appuser
COPY --from=build /app/app.jar app.jar
USER appuser
EXPOSE 8080
# Render injects PORT; locally it falls back to 8080.
ENTRYPOINT ["sh", "-c", "SERVER_PORT=${PORT:-8080} exec java -XX:MaxRAMPercentage=75 -jar app.jar"]
