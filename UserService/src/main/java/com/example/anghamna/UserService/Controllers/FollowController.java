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
    public ResponseEntity<?> follow(@RequestParam int followerId, @RequestParam int followedId) {
        if (followService.follow(followerId, followedId)) {
            return ResponseEntity.ok("Followed successfully");
        } else {
            return ResponseEntity.badRequest().body("Already following or invalid action");
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestParam int followerId, @RequestParam int followedId) {
        if (followService.unfollow(followerId, followedId)) {
            return ResponseEntity.ok("Unfollowed successfully");
        } else {
            return ResponseEntity.badRequest().body("Not currently following");
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable int userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable int userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
