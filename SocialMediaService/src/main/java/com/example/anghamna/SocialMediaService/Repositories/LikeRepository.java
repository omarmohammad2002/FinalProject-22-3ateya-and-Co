package com.example.anghamna.SocialMediaService.Repositories;

import com.example.anghamna.SocialMediaService.Models.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends MongoRepository<Like, String> {
    Optional<Like> findByPostIdAndUserId(String postId, UUID userId);
}
