package com.example.anghamna.SocialMediaService.Repositories;


import com.example.anghamna.SocialMediaService.Models.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByVisibility(String visibility);
    List<Post> findByUserId(UUID userId);
    List<Post> findByUserIdIn(List<UUID> userIds);
}

