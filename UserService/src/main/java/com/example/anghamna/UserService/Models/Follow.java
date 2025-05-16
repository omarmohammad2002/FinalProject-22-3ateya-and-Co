package com.example.anghamna.UserService.Models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="follows")
@IdClass(FollowId.class)
public class Follow {

    @Id
    @Column(name = "follower_id")
    private int followerId;
    @Id
    @Column(name = "followed_id")
    private int followedId;

    @ManyToOne
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    private User followed;

    public int getFollowed_id() {
        return followedId;
    }
    public void setFollowed_id(int followed_id) {
        this.followedId = followed_id;
    }
    public int getFollower_id() {
        return followerId;
    }
    public void setFollower_id(int follower_id) {
        this.followerId = follower_id;
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
