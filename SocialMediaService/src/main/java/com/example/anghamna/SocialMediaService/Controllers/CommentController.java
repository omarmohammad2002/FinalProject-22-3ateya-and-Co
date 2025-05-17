package com.example.anghamna.SocialMediaService.Controllers;

import com.example.anghamna.SocialMediaService.Models.Comment;
import com.example.anghamna.SocialMediaService.Services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /** Create a new comment for a post */
    @PostMapping("/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable String postId,
                                                 @RequestBody String commentText,
                                                 @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Comment created = commentService.createComment(postId, commentText, userId);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> editComment(@PathVariable String id,
                                               @RequestBody String newText,
                                               @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Optional<Comment> updated = commentService.editComment(id, newText, userId);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id,
                                              @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        boolean deleted = commentService.deleteComment(id, userId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
