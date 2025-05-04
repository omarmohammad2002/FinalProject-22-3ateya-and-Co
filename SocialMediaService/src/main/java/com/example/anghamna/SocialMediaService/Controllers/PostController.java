// File: PostController.java
package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    /** Create a post */
    @PostMapping
    public Post create(@RequestParam String userId,
                       @RequestParam String content,
                       @RequestParam(defaultValue = "public") String visibility) {
        return postService.createPost(userId, content, visibility);
    }

    /** List all public posts */
    @GetMapping("/public")
    public List<Post> listPublic() {
        return postService.getAllPublicPosts();
    }

    /** List posts for a given user */
    @GetMapping("/user/{userId}")
    public List<Post> listByUser(@PathVariable String userId) {
        return postService.getPostsByUser(userId);
    }

    /** Update a post (full payload) */
    @PutMapping
    public Post update(@RequestBody Post post) {
        return postService.updatePost(post);
    }

    /** Delete a post */
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable String postId) {
        postService.deletePost(postId);
    }
}
