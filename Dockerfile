FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

ARG JAR_FILE=build/libs/ticket-api-server-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]