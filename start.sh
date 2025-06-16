
echo "Building the jar with Gradle..."
./gradlew clean build -x test || { echo "Gradle build failed!"; exit 1; }

echo "Building Docker images..."
docker-compose build || { echo "Docker build failed!"; exit 1; }

echo "Starting containers..."
docker-compose up -d || { echo "Docker compose up failed!"; exit 1; }

echo "All done! Containers are up and running."