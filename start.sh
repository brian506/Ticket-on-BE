#!/bin/bash

API_MODULE_DIR="./ticket-api-server"
QUEUE_MODULE_DIR="./ticket-queue-server"

echo "Building the jar with Gradle..."
./gradlew -p "$API_MODULE_DIR" clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}

echo "Building the jar with Gradle..."
./gradlew -p "$QUEUE_MODULE_DIR" clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}

echo "Building Docker images..."
docker compose -f docker-compose-local.yml build --no-cache || {
  echo "Docker build failed!"
  exit 1
}

echo "Starting containers..."
docker compose -f docker-compose-local.yml up -d || {
  echo "Docker compose up failed!"
  exit 1
}

echo "All done! Containers are up and running."