package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Comment;
import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Repositories.CommentRepository;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    /** Create a comment only if the post exists */
    public Comment createComment(String postId, String commentText, UUID userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post with ID " + postId + " does not exist.");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setCommentText(commentText);
        comment.setUserId(userId);

        Comment savedComment = commentRepository.save(comment);

        // Increment comment count
        Post post = postOpt.get();
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return savedComment;
    }

    /** Retrieve a comment by its ID */
    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    /** Edit a comment only if user owns it and post exists */
    public Optional<Comment> editComment(String id, String newText, UUID userId) {
        Optional<Comment> optionalComment = commentRepository.findById(id)
                .filter(comment -> comment.getUserId().equals(userId));

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();

            // Check if the associated post still exists
            if (postRepository.existsById(comment.getPostId())) {
                comment.setCommentText(newText);
                commentRepository.save(comment);
                return Optional.of(comment);
            } else {
                throw new IllegalArgumentException("Associated post does not exist for this comment.");
            }
        }
        return Optional.empty();
    }

    /** Delete a comment only if user owns it and post exists */
    public boolean deleteComment(String id, UUID userId) {
        Optional<Comment> commentToDelete = commentRepository.findById(id)
                .filter(comment -> comment.getUserId().equals(userId));

        if (commentToDelete.isPresent()) {
            Comment comment = commentToDelete.get();

            Optional<Post> postOpt = postRepository.findById(comment.getPostId());
            if (postOpt.isEmpty()) {
                throw new IllegalArgumentException("Associated post does not exist for this comment.");
            }

            // Decrement comment count safely
            Post post = postOpt.get();
            int currentCount = post.getCommentsCount();
            post.setCommentsCount(Math.max(0, currentCount - 1));
            postRepository.save(post);

            commentRepository.delete(comment);
            return true;
        }
        return false;
    }
}
