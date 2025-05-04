package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Like;
import com.example.anghamna.SocialMediaService.Repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    /** Check if a user liked a post (cached) */
    @Cacheable(value = "likeStatus", key = "#postId + '_' + #userId")
    public boolean isPostLikedByUser(String postId, String userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    /** Like a post and evict the cached like status */
    @CacheEvict(value = "likeStatus", key = "#postId + '_' + #userId")
    public void likePost(String postId, String userId) {
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(
                like -> {
                    // already liked — do nothing
                },
                () -> likeRepository.save(new Like(postId, userId))
        );
    }

    /** Unlike a post and evict the cached like status */
    @CacheEvict(value = "likeStatus", key = "#postId + '_' + #userId")
    public void unlikePost(String postId, String userId) {
        likeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(likeRepository::delete);
    }

    // --- Command Pattern interfaces and implementations ---

    public interface LikeCommand {
        void execute();
    }

    public class LikePostCommand implements LikeCommand {
        private String postId;
        private String userId;

        public LikePostCommand(String postId, String userId) {
            this.postId = postId;
            this.userId = userId;
        }

        @Override
        public void execute() {
            likePost(postId, userId);
        }
    }

    public class UnlikePostCommand implements LikeCommand {
        private String postId;
        private String userId;

        public UnlikePostCommand(String postId, String userId) {
            this.postId = postId;
            this.userId = userId;
        }

        @Override
        public void execute() {
            unlikePost(postId, userId);
        }
    }
}
