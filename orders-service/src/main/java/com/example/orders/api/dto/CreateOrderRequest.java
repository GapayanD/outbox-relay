package com.example.orders.api.dto;

public record CreateOrderRequest(String customerId, java.util.List<String> skus) { }
