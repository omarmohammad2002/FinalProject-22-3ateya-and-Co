package com.example.anghamna.SocialMediaService.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String postId;
    private UUID userId;
    private String commentText;
    private Date timestamp;

    public Comment() {
        this.timestamp = new Date();
    }

    public Comment(String postId, UUID userId, String commentText) {
        this.postId = postId;
        this.userId = userId;
        this.commentText = commentText;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
