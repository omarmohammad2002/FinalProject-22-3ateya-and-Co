package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Comment;
import com.example.anghamna.SocialMediaService.Repositories.CommentRepository;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {
     // Inject CacheManager to handle cache programmatically
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository; // Inject the PostRepository

   // @CachePut(value = "comments", key = "#result.id")
   @CachePut(value = "comments", key = "#result.id")
   public Comment createComment(Comment comment) {
       Comment savedComment = commentRepository.save(comment);

       // Update the comment count of the associated post
       postRepository.findById(comment.getPostId()).ifPresent(post -> {
           post.setCommentsCount(post.getCommentsCount() + 1); // Increase the comment count
           postRepository.save(post); // Save the updated post
       });



       return savedComment;
   }

    @Cacheable(value = "comments", key = "#id")
    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    @CachePut(value = "comments", key = "#id")
    public Optional<Comment> editComment(String id, String newText) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setCommentText(newText);
            commentRepository.save(comment);
            return Optional.of(comment);
        }
        return Optional.empty();
    }

    @CacheEvict(value = "comments", key = "#id")
    public boolean deleteComment(String id) {
        Optional<Comment> commentToDelete = commentRepository.findById(id);
        if (commentToDelete.isPresent()) {
            Comment comment = commentToDelete.get();

            // Decrease the comment count of the associated post
            postRepository.findById(comment.getPostId()).ifPresent(post -> {
                int currentCommentCount = post.getCommentsCount();
                post.setCommentsCount(Math.max(0, currentCommentCount - 1)); // Prevent negative values
                postRepository.save(post); // Save the updated post
              
            });

            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

}
