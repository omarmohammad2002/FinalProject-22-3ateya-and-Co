package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Like;
import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Repositories.LikeRepository;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    /** Check if the user has already liked the post */
    public boolean isPostLikedByUser(String postId, UUID userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    /** Like a post if it exists and hasn't been liked by this user yet */
    public void likePost(String postId, UUID userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post with ID " + postId + " does not exist.");
        }

        likeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(
                like -> {}, // Already liked, do nothing
                () -> {
                    likeRepository.save(new Like(postId, userId));
                    Post post = postOpt.get();
                    post.setLikesCount(post.getLikesCount() + 1);
                    postRepository.save(post);
                }
        );
    }

    /** Unlike a post only if the user has previously liked it */
    public void unlikePost(String postId, UUID userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post with ID " + postId + " does not exist.");
        }

        Optional<Like> likeOpt = likeRepository.findByPostIdAndUserId(postId, userId);
        if (likeOpt.isEmpty()) {
            throw new IllegalStateException("User has not liked this post and cannot unlike it.");
        }

        // Proceed with unlike
        likeRepository.delete(likeOpt.get());

        Post post = postOpt.get();
        int currentLikes = post.getLikesCount();
        post.setLikesCount(Math.max(0, currentLikes - 1));
        postRepository.save(post);
    }

    // ----------------------------
    // Command Pattern Implementation
    // ----------------------------

    public interface LikeCommand {
        void execute();
    }

    public class DynamicLikeCommand implements LikeCommand {
        private final String postId;
        private final UUID userId;
        private final String action; // "like" or "unlike"

        public DynamicLikeCommand(String action, String postId, UUID userId) {
            this.action = action;
            this.postId = postId;
            this.userId = userId;
        }

        @Override
        public void execute() {
            try {
                Method method = LikeService.class.getMethod(action + "Post", String.class, UUID.class);
                method.invoke(LikeService.this, postId, userId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute action: " + action, e);
            }
        }
    }
}
