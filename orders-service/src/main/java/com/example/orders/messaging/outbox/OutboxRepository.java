package com.example.orders.messaging.outbox;

public interface OutboxRepository extends org.springframework.data.jpa.repository.JpaRepository<OutboxEvent, java.util.UUID> { }
