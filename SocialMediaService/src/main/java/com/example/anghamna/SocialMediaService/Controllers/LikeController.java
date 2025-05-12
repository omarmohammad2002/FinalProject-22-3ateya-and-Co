package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable String postId, @RequestParam String userId) {
        LikeService.LikeCommand command = likeService.new LikePostCommand(postId, userId);
        command.execute();
        return "Post liked successfully.";
    }

    @DeleteMapping("/{postId}/unlike")
    public String unlikePost(@PathVariable String postId, @RequestParam String userId) {
        LikeService.LikeCommand command = likeService.new UnlikePostCommand(postId, userId);
        command.execute();
        return "Post unliked successfully.";
    }

    @GetMapping("/{postId}/status")
    public String getLikeStatus(@PathVariable String postId, @RequestParam String userId) {
        boolean liked = likeService.isPostLikedByUser(postId, userId);
        return liked ? "Liked" : "Not Liked";
    }
}
