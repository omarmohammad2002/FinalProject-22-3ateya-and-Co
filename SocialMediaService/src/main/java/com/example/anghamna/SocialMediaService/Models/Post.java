package com.example.anghamna.SocialMediaService.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String userId;
    private String content;
    private Date timestamp;
    private int likesCount;
    private int commentsCount;
    private String visibility; // "public" or "private"

    private Post(Builder b) {
        this.userId       = b.userId;
        this.content      = b.content;
        this.visibility   = b.visibility;
        this.timestamp    = new Date();
        this.likesCount   = 0;
        this.commentsCount= 0;
    }
    public Post() {}

    // Builder
    public static class Builder {
        private String userId;
        private String content;
        private String visibility = "public";

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Post build() {
            if (userId == null || content == null) {
                throw new IllegalStateException("userId and content are required");
            }
            return new Post(this);
        }
    }

    // --- getters & setters ---
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    public String getVisibility() { return visibility; }
}
