package com.example.orders.service;

@org.springframework.stereotype.Service
public class OrderService {
    @org.springframework.transaction.annotation.Transactional public void create() { /* save order + outbox row */ } }
