# syntax=docker/dockerfile:1

# ---- 1. aşama: Derleme (Maven + JDK olan büyük image) ----
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /src
COPY . .
ARG MODULE
# -pl: sadece bu servisi derle, -am: ihtiyaç duyduğu modülleri (common vb.) de derle
# cache mount: indirilen Maven bağımlılıkları build'ler arasında saklanır
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -pl ${MODULE} -am package -DskipTests

# ---- 2. aşama: Çalıştırma (sadece JRE + jar) ----
FROM eclipse-temurin:25-jre
WORKDIR /app
ARG MODULE
COPY --from=build /src/${MODULE}/target/*.jar app.jar
RUN useradd --system --uid 1001 appuser
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]