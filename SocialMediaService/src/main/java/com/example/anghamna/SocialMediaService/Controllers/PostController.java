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
        UUID userId = UUID.fromString(userIdCookie);
        return postService.createPost(userId, content, visibility);
    }

    /** List all public posts */
    @GetMapping("/public")
    public List<Post> listPublic() {
        System.out.println("Controller");
        return postService.getAllPublicPosts();
    }

    /** List posts for the logged-in user */
    @GetMapping("/user")
    public List<Post> listByUser(@CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        return postService.getPostsByUser(userId);
    }

    /** Update a post (postId from path, content and visibility from body, userId from cookie) */
    @PutMapping("/{postId}")
    public ResponseEntity<Post> update(@PathVariable String postId,
                                       @RequestBody UpdatePostRequest request,
                                       @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Post updatedPost = postService.updatePost(postId, userId, request.getContent(), request.getVisibility());
        return ResponseEntity.ok(updatedPost);
    }

    /** Delete a post (requires userId from cookie to verify ownership) */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable String postId,
                                       @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    /** Request Body class for update */
    public static class UpdatePostRequest {
        private String content;
        private String visibility = "public"; // Default visibility

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }
}
