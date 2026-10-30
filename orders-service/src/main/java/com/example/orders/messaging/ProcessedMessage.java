package com.example.orders.messaging;

@jakarta.persistence.Entity @jakarta.persistence.Table(name = "processed_messages", schema = "orders")
public class ProcessedMessage { @jakarta.persistence.Id String messageId; }
