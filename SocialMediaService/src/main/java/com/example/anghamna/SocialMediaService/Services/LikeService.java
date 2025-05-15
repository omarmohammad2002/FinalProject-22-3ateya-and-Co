package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Like;
import com.example.anghamna.SocialMediaService.Repositories.LikeRepository;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Cacheable(value = "likeStatus", key = "#postId + '_' + #userId")
    public boolean isPostLikedByUser(String postId, String userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    @CacheEvict(value = "likeStatus", key = "#postId + '_' + #userId")
    public void likePost(String postId, String userId) {
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(
                like -> {},
                () -> {
                    likeRepository.save(new Like(postId, userId));
                    postRepository.findById(postId).ifPresent(post -> {
                        post.setLikesCount(post.getLikesCount() + 1);
                        postRepository.save(post);
                    });
                }
        );
    }

    @CacheEvict(value = "likeStatus", key = "#postId + '_' + #userId")
    public void unlikePost(String postId, String userId) {
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
            likeRepository.delete(like);
            postRepository.findById(postId).ifPresent(post -> {
                int currentLikes = post.getLikesCount();
                post.setLikesCount(Math.max(0, currentLikes - 1));
                postRepository.save(post);
            });
        });
    }

    // ----------------------------
    // Command Pattern with Reflection Inside
    // ----------------------------

    public interface LikeCommand {
        void execute();
    }

    public class DynamicLikeCommand implements LikeCommand {
        private final String postId;
        private final String userId;
        private final String action; // "like" or "unlike"

        public DynamicLikeCommand(String action, String postId, String userId) {
            this.action = action;
            this.postId = postId;
            this.userId = userId;
        }

        @Override
        public void execute() {
            try {
                Method method = LikeService.class.getMethod(action + "Post", String.class, String.class);
                method.invoke(LikeService.this, postId, userId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute action: " + action, e);
            }
        }
    }
}
