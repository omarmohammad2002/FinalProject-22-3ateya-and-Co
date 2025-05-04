package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Comment;
import com.example.anghamna.SocialMediaService.Repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @CachePut(value = "comments", key = "#result.id")
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
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
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}