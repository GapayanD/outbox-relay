package com.example.orders.messaging;

@org.springframework.stereotype.Component
public class OrderEnrichedListener {
    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "orders.order.enriched") void on(String body) { } }
