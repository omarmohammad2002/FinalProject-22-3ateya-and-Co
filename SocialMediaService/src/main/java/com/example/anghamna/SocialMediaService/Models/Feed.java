package com.example.anghamna.SocialMediaService.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "feeds")
public class Feed {

    @Id
    private String id;

    private String userId;        // Recipient of the feed item
    private String postId;        // Can refer to a post, song, playlist, etc.
    private String type;          // Type of feed item: "post", "song", etc.
    private String sourceUserId;  // The user who triggered this feed
    private Date timestamp;

    // No-argument constructor
    public Feed() {
    }

    // All-arguments constructor
    public Feed(String id, String userId, String postId, String type, String sourceUserId, Date timestamp) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.type = type;
        this.sourceUserId = sourceUserId;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
