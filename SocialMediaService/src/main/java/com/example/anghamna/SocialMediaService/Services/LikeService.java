package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Models.Like;
import com.example.anghamna.SocialMediaService.Repositories.LikeRepository;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;

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
                    // Already liked — do nothing
                },
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
                post.setLikesCount(Math.max(0, currentLikes - 1)); // Prevent negative values
                postRepository.save(post);
            });
        });
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
