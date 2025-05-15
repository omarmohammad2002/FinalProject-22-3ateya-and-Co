// File: PostController.java
package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Models.Feed;
import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Services.FeedService;
import com.example.anghamna.SocialMediaService.Services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feeds")
public class FeedController {

    private FeedService feedService;
    @Autowired
    private PostService postService;  // This contains your getNewsFeedForUser method

    @GetMapping("/{userId}")
    public List<Post> getNewsFeed(@PathVariable String userId) {
        return postService.getNewsFeedForUser(userId);
    }
    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping
    public Feed createFeed(@RequestBody Feed feed) {
        return feedService.createFeed(feed);
    }

//    @GetMapping("/{userId}")
//    public List<Feed> getFeedForUser(@PathVariable String userId) {
//        return feedService.getFeedsForUser(userId);
//    }
}
