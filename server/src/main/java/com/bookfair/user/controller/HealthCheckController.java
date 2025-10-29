package com.bookfair.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health")
    public String healthCheck() {
        return "✅ User Service API is up and running!";
    }
}
