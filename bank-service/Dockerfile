FROM azul-zulu:21 AS builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

FROM azul/zulu-openjdk:21-jre-headless
WORKDIR /app
COPY --from=builder application.jar .
EXPOSE 2530
ENTRYPOINT ["java", "-jar", "application.jar"]