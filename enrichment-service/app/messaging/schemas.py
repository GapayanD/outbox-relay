from pydantic import BaseModel


class OrderCreated(BaseModel):
    order_id: str
    customer_id: str


class OrderEnriched(BaseModel):
    order_id: str
    fraud_score: float
