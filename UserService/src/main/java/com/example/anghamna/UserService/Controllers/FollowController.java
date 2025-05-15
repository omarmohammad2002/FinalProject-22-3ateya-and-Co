package com.example.anghamna.UserService.Controllers;

import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Services.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestParam UUID followerId, @RequestParam UUID followedId) {

        if (followService.follow(followerId, followedId)) {
            return ResponseEntity.ok("Followed successfully");
        } else {
            return ResponseEntity.badRequest().body("Already following or invalid action");
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestParam UUID followerId, @RequestParam UUID followedId) {
        if (followService.unfollow(followerId, followedId)) {
            return ResponseEntity.ok("Unfollowed successfully");
        } else {
            return ResponseEntity.badRequest().body("Not currently following");
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
    @GetMapping("/{userId}/following/ids")
    public ResponseEntity<List<UUID>> getFollowingIds(@PathVariable UUID userId) {
        List<UUID> followingIds = followService.getFollowingIds(userId);
        return ResponseEntity.ok(followingIds);
    }

}
