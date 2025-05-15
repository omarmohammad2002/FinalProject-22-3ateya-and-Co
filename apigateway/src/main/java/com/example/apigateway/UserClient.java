package com.example.apigateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8081/userapi")
public interface UserClient {
    @GetMapping("/session/validate")
    boolean validateSession(@RequestParam("sessionId") String sessionId);
}

