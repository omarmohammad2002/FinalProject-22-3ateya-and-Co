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
