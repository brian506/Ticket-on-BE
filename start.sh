echo "Building the jar with Gradle..."
./gradlew clean build -x test || { echo "Gradle build failed!"; exit 1; }

echo "Building Docker images..."
docker compose -f docker-compose-local.yml build --no-cache || { echo "Docker build failed!"; exit 1; }

echo "Starting containers..."
docker compose -f docker-compose-local.yml up -d || { echo "Docker compose up failed!"; exit 1; }

echo "All done! Containers are up and running."
