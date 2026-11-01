# outbox-relay

An event-driven order pipeline built as two services in two languages, connected by RabbitMQ.

- **`orders-service`** (Java 25, Spring Boot 4.1): owns the order domain, exposes the REST API, and publishes events through a transactional outbox.
- **`enrichment-service`** (Python 3.12, FastAPI): consumes `order.created` events, enriches each order (fraud score, address normalization), and publishes `order.enriched` back.

The project exists to practice service boundaries, contract design and the failure modes of messaging (duplicates, retries, dead letters), and to compare Java and Python on the same problem.

> **Status:** early scaffold. Infrastructure (Postgres, Redis, RabbitMQ) runs; the services are stubs with the right structure, migrations and contracts in place. See [Roadmap](#roadmap).

## Architecture

```mermaid
flowchart LR
    FE[React frontend] -- REST --> OS
    subgraph Java
        OS[orders-service<br/>Spring Boot]
    end
    subgraph Python
        ES[enrichment-service<br/>FastAPI]
    end
    OS -- order.created --> MQ{{RabbitMQ<br/>exchange: orders}}
    MQ -- enrichment.order.created --> ES
    ES -- order.enriched --> MQ
    MQ -- orders.order.enriched --> OS
    OS --- PG[(Postgres<br/>schema: orders)]
    ES --- PG2[(Postgres<br/>schema: enrichment)]
```

### Message flow (first vertical slice)

```
POST /orders
  -> orders row + outbox row (one DB transaction)
  -> OutboxPublisher polls the outbox and publishes order.created
  -> queue enrichment.order.created
  -> Python consumer -> enrichment pipeline -> publishes order.enriched
  -> queue orders.order.enriched
  -> OrderEnrichedListener sets order status to ENRICHED
```

### Design decisions

- **Contracts are the only shared dependency.** The Java and Python services never import each other's code. Event payloads are validated against the JSON Schemas in `contracts/events/`, and the REST API is described in `contracts/openapi/`.
- **Transactional outbox.** The order and its outbox row are written in one transaction, so a committed order can never lose its event (no dual-write problem).
- **Idempotent consumers.** RabbitMQ delivers at least once, so both sides record handled message IDs in a `processed_messages` table and skip duplicates.
- **One schema per service.** Postgres has an `orders` schema and an `enrichment` schema, each owned by its own database user. Services do not share tables.
- **Dead-letter topology lives in config.** Exchanges, queues, bindings and the DLQ are declared in `infra/rabbitmq/definitions.json`, so every environment starts with the same broker setup.

### Events and routing

| Event | Routing key | Queue | Payload (see `contracts/events/`) |
|---|---|---|---|
| `order.created` v1 | `order.created` | `enrichment.order.created` | `order_id`, `customer_id` |
| `order.enriched` v1 | `order.enriched` | `orders.order.enriched` | `order_id`, `fraud_score` |

Rejected or failed messages are dead-lettered through `orders.dlx` into `orders.dlq`.

## Tech stack

| Area | Choice |
|---|---|
| Orders service | Java 25, Spring Boot 4.1, Spring Data JPA, Spring Security, Spring AMQP, Flyway |
| Enrichment service | Python 3.12, FastAPI, aio-pika, SQLalchemy 2.0 (async), Alembic, Pydantic v2 |
| Infrastructure | PostgreSQL 16, RabbitMQ 3.13, Redis 7 |
| Observability | Micrometer + Prometheus, `prometheus-client`, Grafana |
| Testing | JUnit 5, Testcontainers, pytest |
| CI | GitHub Actions, path-filtered per service |

## Repository layout

```
outbox-relay/
├── contracts/            # OpenAPI spec + JSON Schemas for events (source of truth)
├── orders-service/       # Java / Spring Boot (Maven)
├── enrichment-service/   # Python / FastAPI (uv)
├── frontend/             # React client (generated from the OpenAPI spec)
├── infra/                # Postgres init, RabbitMQ definitions, Prometheus, Grafana
├── .github/workflows/    # one workflow per service + contracts lint
├── docker-compose.yml    # postgres, redis, rabbitmq
└── Makefile
```

## Getting started

### Prerequisites

- Docker with Compose v2
- JDK 25 and Maven 3.9+
- Python 3.12 and [uv](https://docs.astral.sh/uv/)
- `make`

### 1. Start the infrastructure

```bash
cp .env.example .env
make up
```

`.env.example` lists the variables the services read; copy it as a reference for your own setup. `make up` currently starts Postgres, Redis and RabbitMQ only.

Check that it worked:

- RabbitMQ UI at <http://localhost:15672> (`guest` / `guest`): the `orders` and `orders.dlx` exchanges and the three queues should exist.
- Postgres on `localhost:5432`:
  ```bash
  docker compose exec postgres psql -U postgres -d orders -c '\dn'
  ```
  You should see the `orders` and `enrichment` schemas.

### 2. Run `orders-service`

```bash
cd orders-service
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The `dev` profile points the service at `localhost` instead of the Docker hostnames. Flyway applies the migrations in `src/main/resources/db/migration/` on startup. Health and metrics are exposed at `/actuator/health` and `/actuator/prometheus`.

> `./mvnw` is currently a shim that calls your installed `mvn`. Generate the real wrapper with `mvn wrapper:wrapper`.

### 3. Run `enrichment-service`

```bash
cd enrichment-service
uv sync
RABBITMQ_URL=amqp://guest:guest@localhost:5672/ \
DATABASE_URL=postgresql+asyncpg://enrichment_svc:enrichment@localhost:5432/orders \
uv run uvicorn app.main:app --reload
```

Endpoints: `/healthz` and `/metrics` on port 8000.

## Testing and linting

```bash
make test    # orders-service (mvn test) + enrichment-service (pytest)
make lint    # ruff on enrichment-service
```

Integration tests in `orders-service` use Testcontainers (Postgres and RabbitMQ), so Docker must be running. `tests/test_contract.py` in the Python service checks the event schemas in `contracts/events/`.

## Configuration

| Variable | Used by | Default |
|---|---|---|
| `RABBITMQ_URL` | enrichment-service | `amqp://guest:guest@rabbitmq:5672/` |
| `DATABASE_URL` | enrichment-service | `postgresql+asyncpg://enrichment_svc:enrichment@postgres:5432/orders` |
| `SPRING_PROFILES_ACTIVE` | orders-service | none (use `dev` locally) |
| `JWT_SECRET` | orders-service (planned) | `change-me` in `.env.example`, never use it outside local dev |

The database users and passwords in `infra/postgres/init.sql` and the `guest` broker user are for local development only.

## CI

Each workflow runs only when its own paths change:

| Workflow | Triggers on | Runs |
|---|---|---|
| `orders-service.yml` | `orders-service/**`, `contracts/**` | `mvn verify` on JDK 25 |
| `enrichment-service.yml` | `enrichment-service/**`, `contracts/**` | `uv sync`, ruff, pytest |
| `contracts.yml` | `contracts/**` | OpenAPI lint, JSON Schema compile |

## Roadmap

- [x] Repo scaffold, contracts, Flyway migrations, broker topology, CI workflows
- [ ] `POST /orders` and `GET /orders/{id}` with JPA and validation
- [ ] Outbox publisher and the full `order.created` -> `order.enriched` round trip
- [ ] Idempotent consumers and retry/DLQ handling on both sides
- [ ] Contract tests on both sides, Testcontainers integration tests
- [ ] JWT authentication and role-based access in Spring Security
- [ ] Both services in `docker-compose.yml`, plus Prometheus and Grafana dashboards
- [ ] Frontend client generated from `contracts/openapi/orders-api.yaml`

## Notes

- Redis is in the compose file but nothing uses it yet. It is reserved for caching or rate limiting later.
- Boot 4 changed several starter and test artifact names compared to 3.x, so older tutorials may not match this project exactly.
