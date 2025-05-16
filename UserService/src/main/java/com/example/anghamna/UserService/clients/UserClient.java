package com.example.anghamna.UserService.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-service", url="http://localhost:8091/availability")
public interface UserClient {

    @GetMapping("/check/{roomType}/{nights}")
    boolean check(@PathVariable String roomType, @PathVariable int nights);

}