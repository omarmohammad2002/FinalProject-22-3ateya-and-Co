// File: PostController.java
package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Models.Feed;
import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Services.FeedService;
import com.example.anghamna.SocialMediaService.Services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feeds")
public class FeedController {

    //private FeedService feedService;
    @Autowired
    private PostService postService;  // This contains your getNewsFeedForUser method

    @GetMapping
    public ResponseEntity<?> getNewsFeed(@CookieValue(value = "USER_ID", required = false) String userIdCookie) {
        try {
            System.out.println("Followings for user ssssssssssssssssssssssssssssss");
            if (userIdCookie == null || userIdCookie.isEmpty()) {
                return ResponseEntity.badRequest().body("USER_ID cookie is missing.");
            }

            UUID userId = UUID.fromString(userIdCookie);
            List<Post> feed = postService.getNewsFeedForUser(userId);

            return ResponseEntity.ok(feed);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid USER_ID format. Please provide a valid UUID.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
    //   @Autowired
//    public FeedController(FeedService feedService) {
//        this.feedService = feedService;
//    }
//
//    @PostMapping
//    public Feed createFeed(@RequestBody Feed feed) {
//        return feedService.createFeed(feed);
//    }

//    @GetMapping("/{userId}")
//    public List<Feed> getFeedForUser(@PathVariable String userId) {
//        return feedService.getFeedsForUser(userId);
//    }
}
