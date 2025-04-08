# Variables
APP_RUN=mvn spring-boot:run
DOCKER_COMPOSE=docker-compose

# Targets

start-db:
	@echo "🟡 Starting PostgresSQL..."
	@$(DOCKER_COMPOSE) up -d

stop-db:
	@echo "🔴 Stopping PostgresSQL..."
	@$(DOCKER_COMPOSE) down

run-app:
	@echo "🟢 Running Spring Boot app..."
	@$(APP_RUN)

dev: start-db run-app

reset:
	@echo "🧨 Stopping & removing DB..."
	@$(DOCKER_COMPOSE) down -v

