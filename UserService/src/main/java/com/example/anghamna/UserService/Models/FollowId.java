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

}

