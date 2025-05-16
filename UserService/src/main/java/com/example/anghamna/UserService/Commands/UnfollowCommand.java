package com.example.anghamna.UserService.Commands;

import com.example.anghamna.UserService.Events.EventPublisher;
import com.example.anghamna.UserService.Repositories.FollowRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UnfollowCommand implements CommandInterface {
    private final int followerId;
    private final int followedId;
    private final FollowRepository followRepository;
    private final EventPublisher eventPublisher;

    public UnfollowCommand(int followerId, int followedId, FollowRepository followRepository, EventPublisher eventPublisher) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.followRepository = followRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public boolean execute() {
        if (!followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            return false;
        }

        followRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);

//        eventPublisher.publish("user.unfollowed", Map.of(
//                "followerId", followerId.toString(),
//                "followedId", followedId.toString(),
//                "timestamp", Instant.now().toString()
//        ));

//        eventPublisher.publishUnfollowEvent(Integer.toString(followerId), Integer.toString(followedId));

        return true;
    }


}


