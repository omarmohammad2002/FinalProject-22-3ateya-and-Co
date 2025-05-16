package com.example.apigateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://user-service:8080/userapi/sessions")
public interface UserClient {
    @GetMapping("/validate")
    boolean validateSession(@RequestParam("sessionId") int sessionId);
}

