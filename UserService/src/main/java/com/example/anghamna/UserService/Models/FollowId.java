package com.example.anghamna.UserService.Models;

import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;

public class FollowId implements Serializable {

    private UUID followerId;
    private UUID followedId;

    public UUID getFollowerId() {
        return followerId;
    }
    public void setFollowerId(UUID followerId) {
        this.followerId = followerId;
    }
    public UUID getFollowedId() {
        return followedId;
    }
    public void setFollowedId(UUID followedId) {
        this.followedId = followedId;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowId)) return false;
        FollowId that = (FollowId) o;
        return Objects.equals(follower_id, that.follower_id) &&
                Objects.equals(followed_id, that.followed_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower_id, followed_id);
    }

}

