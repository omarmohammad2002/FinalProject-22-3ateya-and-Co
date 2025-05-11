package com.example.anghamna.UserService.Commands;

import com.example.anghamna.UserService.Events.EventPublisher;
import com.example.anghamna.UserService.Models.Follow;
import com.example.anghamna.UserService.Repositories.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class FollowCommand implements CommandInterface {
    private final UUID followerId;
    private final UUID followedId;
    private final FollowRepository followRepository;
    private final EventPublisher eventPublisher;


    public FollowCommand(UUID followerId, UUID followedId, FollowRepository followRepository, EventPublisher eventPublisher) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.followRepository = followRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean execute() {
        if (followerId.equals(followedId) || followRepository.existsByFollower_idAndFollowed_id(followerId, followedId)) {
            return false;
        }

        Follow follow = new Follow();
        follow.setFollower_id(followerId);
        follow.setFollowed_id(followedId);
        follow.setCreated_at(Instant.now());
        followRepository.save(follow);

        eventPublisher.publish("user.followed", Map.of(
                "followerId", followerId.toString(),
                "followedId", followedId.toString(),
                "timestamp", Instant.now().toString()
        ));

        return true;
    }
}
