package com.example.anghamna.StreamingService.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081/users")
public interface UserClient {

    @GetMapping("/getUserTypeById{id}")
    String getUserTypeById(@PathVariable("id") long id);
}