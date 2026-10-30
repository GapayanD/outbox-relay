package com.example.orders.messaging.outbox;

@jakarta.persistence.Entity @jakarta.persistence.Table(name = "outbox", schema = "orders")
public class OutboxEvent { @jakarta.persistence.Id java.util.UUID id; }
