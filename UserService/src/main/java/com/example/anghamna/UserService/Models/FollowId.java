package com.example.anghamna.UserService.Models;

import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;

public class FollowId implements Serializable {

    private int followerId;
    private int followedId;

    public int getFollowerId() {
        return followerId;
    }
    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }
    public int getFollowedId() {
        return followedId;
    }
    public void setFollowedId(int followedId) {
        this.followedId = followedId;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowId)) return false;
        FollowId that = (FollowId) o;
        return Objects.equals(followerId, that.followerId) &&
                Objects.equals(followedId, that.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }

}

