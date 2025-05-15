package com.example.anghamna.SocialMediaService.Clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "UserService", url = "http://localhost:8080/users")
public interface UserClient {

    @GetMapping("/followings/{userId}")
    List<String> getFollowings(@PathVariable UUID userId);
}
