from fastapi import FastAPI

from app.api import health, metrics

app = FastAPI(title="enrichment-service")
app.include_router(health.router)
app.include_router(metrics.router)
