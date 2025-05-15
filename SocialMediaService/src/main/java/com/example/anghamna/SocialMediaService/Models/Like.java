package com.example.anghamna.SocialMediaService.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "likes")
public class Like {
    @Id
    private String id;
    private String postId;
    private UUID userId;

    public Like(String postId, UUID userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public UUID getUserId() { return userId; }
}
