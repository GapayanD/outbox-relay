package com.example.orders.messaging.events;

public record OrderEnrichedEvent(@com.fasterxml.jackson.annotation.JsonProperty("order_id") String orderId, @com.fasterxml.jackson.annotation.JsonProperty("fraud_score") double fraudScore) { }
