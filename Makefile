.PHONY: up down test lint
up: ; docker compose up -d --build
down: ; docker compose down -v
test: ; cd orders-service && ./mvnw -q test && cd ../enrichment-service && uv run pytest -q
lint: ; cd enrichment-service && uv run ruff check .
