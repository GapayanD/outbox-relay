from pydantic_settings import BaseSettings
class Settings(BaseSettings):
    rabbitmq_url: str = "amqp://guest:guest@rabbitmq:5672/"
    database_url: str = "postgresql+asyncpg://enrichment_svc:enrichment@postgres:5432/orders"
settings = Settings()
