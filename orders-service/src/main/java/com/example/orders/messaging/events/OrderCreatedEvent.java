package com.example.orders.messaging.events;

public record OrderCreatedEvent(@com.fasterxml.jackson.annotation.JsonProperty("order_id") String orderId, @com.fasterxml.jackson.annotation.JsonProperty("customer_id") String customerId) { }
