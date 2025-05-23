package com.example.anghamna.SocialMediaService.Repositories;

import com.example.anghamna.SocialMediaService.Models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByUserId(UUID userId);
}