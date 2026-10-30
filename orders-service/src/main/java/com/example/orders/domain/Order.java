package com.example.orders.domain;

@jakarta.persistence.Entity @jakarta.persistence.Table(name = "orders", schema = "orders")
public class Order {
    @jakarta.persistence.Id java.util.UUID id; @jakarta.persistence.Version long version; }
