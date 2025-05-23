package com.example.anghamna.UserService.Commands;

import com.example.anghamna.UserService.rabbitmq.EventPublisher;
import com.example.anghamna.UserService.Models.Follow;
import com.example.anghamna.UserService.Repositories.FollowRepository;

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
        if (followerId == followedId || followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            return false;
        }

        Follow follow = new Follow();
        follow.setFollower_id(followerId);
        follow.setFollowed_id(followedId);
        followRepository.save(follow);

//        eventPublisher.publish("user.followed", Map.of(
//                "followerId", followerId.toString(),
//                "followedId", followedId.toString(),
//                "timestamp", Instant.now().toString()
//        ));

//        eventPublisher.publishFollowEvent(Integer.toString(followerId), Integer.toString(followedId));

        return true;
    }
}
