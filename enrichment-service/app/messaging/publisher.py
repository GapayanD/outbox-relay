import aio_pika
from aio_pika.abc import AbstractChannel
async def publish(channel: AbstractChannel, key: str, body: bytes) -> None:
    exchange = await channel.get_exchange("orders")
    await exchange.publish(aio_pika.Message(body=body), routing_key=key)
