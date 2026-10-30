package com.example.orders.messaging.outbox;

@org.springframework.stereotype.Component
public class OutboxPublisher {
    @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 1000) void poll() { /* outbox -> broker */ } }
