package com.example.anghamna.UserService.Services;


import com.example.anghamna.UserService.Commands.CommandExecutor;
import com.example.anghamna.UserService.Commands.FollowCommand;
import com.example.anghamna.UserService.Commands.UnfollowCommand;
import com.example.anghamna.UserService.Models.Follow;
import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Repositories.FollowRepository;
import com.example.anghamna.UserService.Repositories.UserRepository;
import com.example.anghamna.UserService.Events.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

//@Service
//public class FollowService {
//
//    private final FollowRepository followRepository;
//    private final UserRepository userRepository;
//    private final EventPublisher eventPublisher;
//
//    @Autowired
//    public FollowService(FollowRepository followRepository, UserRepository userRepository, EventPublisher eventPublisher) {
//        this.followRepository = followRepository;
//        this.userRepository = userRepository;
//        this.eventPublisher = eventPublisher;
//    }
//
//    public boolean follow(UUID followerId, UUID followedId) {
//        if (followerId.equals(followedId) || followRepository.existsByFollower_idAndFollowed_id(followerId, followedId)) {
//            return false;
//        }
//
//        Follow follow = new Follow();
//        follow.setFollower_id(followerId);
//        follow.setFollowed_id(followedId);
//        follow.setCreated_at(Instant.now());
//        followRepository.save(follow);
//
//        eventPublisher.publish("user.followed", Map.of(
//                "followerId", followerId.toString(),
//                "followedId", followedId.toString(),
//                "timestamp", Instant.now().toString()
//        ));
//
//        return true;
//    }
//
//    public boolean unfollow(UUID followerId, UUID followedId) {
//        if (!followRepository.existsByFollower_idAndFollowed_id(followerId, followedId)) {
//            return false;
//        }
//        followRepository.deleteByFollower_idAndFollowed_id(followerId, followedId);
//
//        eventPublisher.publish("user.unfollowed", Map.of(
//                "followerId", followerId.toString(),
//                "followedId", followedId.toString(),
//                "timestamp", Instant.now().toString()
//        ));
//
//        return true;
//    }
//
//    public List<User> getFollowers(UUID userId) {
//        return followRepository.findByFollowed_id(userId).stream()
//                .map(Follow::getFollower)
//                .toList();
//    }
//
//    public List<User> getFollowing(UUID userId) {
//        return followRepository.findByFollower_id(userId).stream()
//                .map(Follow::getFollowed)
//                .toList();
//    }
//}


@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final CommandExecutor commandExecutor;

    @Autowired
    public FollowService(FollowRepository followRepository, UserRepository userRepository,
                         EventPublisher eventPublisher) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.commandExecutor = new CommandExecutor(); // or inject if needed
    }

    public boolean follow(UUID followerId, UUID followedId) {
        FollowCommand command = new FollowCommand(followerId, followedId, followRepository, eventPublisher);
        return commandExecutor.runCommand(command);
    }

    public boolean unfollow(UUID followerId, UUID followedId) {
        UnfollowCommand command = new UnfollowCommand(followerId, followedId, followRepository, eventPublisher);
        return commandExecutor.runCommand(command);
    }

    public List<User> getFollowers(UUID userId) {
        return followRepository.findByFollowed_id(userId).stream()
                .map(Follow::getFollower)
                .toList();
    }

    public List<User> getFollowing(UUID userId) {
        return followRepository.findByFollower_id(userId).stream()
                .map(Follow::getFollowed)
                .toList();
    }
}

