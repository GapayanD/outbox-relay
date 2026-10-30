import aio_pika
from app.config import settings
async def run_consumer() -> None:
    connection = await aio_pika.connect_robust(settings.rabbitmq_url)
