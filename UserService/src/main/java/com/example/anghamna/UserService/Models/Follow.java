package com.example.anghamna.UserService.Models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="follows")
@IdClass(FollowId.class)
public class Follow {

    @Id
    private UUID follower_id;
    @Id
    private UUID followed_id;
    private Instant created_at;

    @ManyToOne
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    private User followed;

    public UUID getFollowed_id() {
        return followed_id;
    }
    public void setFollowed_id(UUID followed_id) {
        this.followed_id = followed_id;
    }
    public UUID getFollower_id() {
        return follower_id;
    }
    public void setFollower_id(UUID follower_id) {
        this.follower_id = follower_id;
    }
    public Instant getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }
    public User getFollower() {
        return follower;
    }
    public void setFollower(User follower) {
        this.follower = follower;
    }
    public User getFollowed() {
        return followed;
    }
    public void setFollowed(User followed) {
        this.followed = followed;
    }
}
