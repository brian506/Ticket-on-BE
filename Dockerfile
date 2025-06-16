FROM --platform=linux/amd64 openjdk:21-jdk-slim

WORKDIR /app

ARG JAR_FILE=build/libs/ticketon-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]