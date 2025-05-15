package com.example.anghamna.StreamingService.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "http://user-service:8080/userapi/user")
public interface UserClient {

    @GetMapping("/userType/{id}")
    String getUserTypeById(@PathVariable("id") UUID id);
}