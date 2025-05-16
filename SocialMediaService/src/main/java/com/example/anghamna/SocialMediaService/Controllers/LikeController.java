package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /** Like a post using Command (internally calls Reflection) */
    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable String postId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        LikeService.LikeCommand command = likeService.new DynamicLikeCommand("like", postId, userId);
        command.execute();
        return "Post liked successfully.";
    }

    /** Unlike a post using Command (internally calls Reflection) */
    @DeleteMapping("/{postId}/unlike")
    public String unlikePost(@PathVariable String postId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        LikeService.LikeCommand command = likeService.new DynamicLikeCommand("unlike", postId, userId);
        command.execute();
        return "Post unliked successfully.";
    }

    @GetMapping("/{postId}/status")
    public String getLikeStatus(@PathVariable String postId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        boolean liked = likeService.isPostLikedByUser(postId, userId);
        return liked ? "Liked" : "Not Liked";
    }
}
