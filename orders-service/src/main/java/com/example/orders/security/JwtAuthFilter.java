package com.example.orders.security;

public class JwtAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {
    @Override protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res, jakarta.servlet.FilterChain chain)
            throws java.io.IOException, jakarta.servlet.ServletException { chain.doFilter(req, res); } }
