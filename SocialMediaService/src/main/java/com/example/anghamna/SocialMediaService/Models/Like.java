package com.example.anghamna.SocialMediaService.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes")
public class Like {
    @Id
    private String id;
    private String postId;
    private String userId;

    public Like(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
}
