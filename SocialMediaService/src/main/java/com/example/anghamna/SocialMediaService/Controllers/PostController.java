// File: PostController.java
package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    /** Create a post */
    @PostMapping
    public Post create(@CookieValue("USER_ID") String userIdCookie,
                       @RequestParam String content,
                       @RequestParam(defaultValue = "public") String visibility) {
        UUID userID = UUID.fromString(userIdCookie);
        return postService.createPost(userID, content, visibility);
    }

    /** List all public posts */
    @GetMapping("/public")
    public List<Post> listPublic() {

            System.out.println("Controller");
            return postService.getAllPublicPosts();


    }

    /** List posts for a given user */
    @GetMapping("/user/{userId}")
    public List<Post> listByUser(@CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
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
