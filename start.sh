#!/bin/bash

API_MODULE_DIR="./ticket-api-server"
#QUEUE_MODULE_DIR="./ticket-queue-server"

echo "Building the jar with Gradle..."
./gradlew -p "$API_MODULE_DIR" clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}
#
#echo "Building the jar with Gradle..."
#./gradlew -p "$QUEUE_MODULE_DIR" clean build -x test || {
#  echo "Gradle build failed!"
#  exit 1
#}

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

echo "Waiting for Kafka Connect to be ready..."
# Kafka Connect API(8083)가 응답할 때까지 10초 간격으로 최대 12번 시도 (2분)
MAX_RETRIES=12
COUNT=0
while [ $COUNT -lt $MAX_RETRIES ]; do
  curl -s -f http://localhost:8083/connectors > /dev/null
  if [ $? -eq 0 ]; then
    echo "Kafka Connect is up!"
    break
  fi
  echo "Kafka Connect is not ready yet. Retrying in 10 seconds..."
  sleep 10
  COUNT=$((COUNT+1))
done

if [ $COUNT -eq $MAX_RETRIES ]; then
  echo "Failed to connect to Kafka Connect after 2 minutes."
  exit 1
fi

echo "Registering Debezium MySQL connector..."

# 템플릿 파일에서 .env 변수를 읽어 실제 JSON 파일을 생성
envsubst < register-debezium.json.template > register-debezium.json

# 생성된 JSON 파일을 사용해 Debezium 커넥터 등록
curl -X POST -H "Content-Type: application/json" \
--data @register-debezium.json \
http://localhost:8083/connectors

echo "Debezium connector registered."

echo "All done! Containers are up and running."