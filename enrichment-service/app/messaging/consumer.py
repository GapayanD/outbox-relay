import aio_pika

from app.config import settings


async def run_consumer() -> None:
    async with await aio_pika.connect_robust(settings.rabbitmq_url):
        ...  # TODO: declare queue, consume enrichment.order.created
