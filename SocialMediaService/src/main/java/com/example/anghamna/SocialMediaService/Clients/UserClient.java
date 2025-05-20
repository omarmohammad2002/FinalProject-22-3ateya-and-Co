package com.example.anghamna.SocialMediaService.Clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "UserService", url = "http://user-service:8080/userapi/follows")
public interface UserClient {

    @GetMapping("{userId}/following/ids")
    List<UUID> getFollowings(@PathVariable UUID userId);
}
