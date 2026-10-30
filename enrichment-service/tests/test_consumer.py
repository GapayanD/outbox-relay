from app.messaging.schemas import OrderCreated
def test_parses_event():
    assert OrderCreated(order_id="1", customer_id="c").order_id == "1"
