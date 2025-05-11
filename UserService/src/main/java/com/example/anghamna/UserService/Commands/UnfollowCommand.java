package com.example.anghamna.UserService.Commands;

import com.example.anghamna.UserService.Events.EventPublisher;
import com.example.anghamna.UserService.Repositories.FollowRepository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UnfollowCommand implements CommandInterface {
    private final UUID followerId;
    private final UUID followedId;
    private final FollowRepository followRepository;
    private final EventPublisher eventPublisher;

    public UnfollowCommand(UUID followerId, UUID followedId, FollowRepository followRepository, EventPublisher eventPublisher) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.followRepository = followRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean execute() {
        if (!followRepository.existsByFollower_idAndFollowed_id(followerId, followedId)) {
            return false;
        }

        followRepository.deleteByFollower_idAndFollowed_id(followerId, followedId);

//        eventPublisher.publish("user.unfollowed", Map.of(
//                "followerId", followerId.toString(),
//                "followedId", followedId.toString(),
//                "timestamp", Instant.now().toString()
//        ));

        eventPublisher.publishUnfollowEvent(followerId.toString(), followedId.toString());

        return true;
    }
}
