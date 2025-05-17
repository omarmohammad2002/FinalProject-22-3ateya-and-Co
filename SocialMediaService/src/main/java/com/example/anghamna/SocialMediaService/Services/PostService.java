package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Clients.UserClient;
import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserClient userClient;

    /** Create a post (Evict caches to refresh data) */
    @CacheEvict(value = {"publicPosts", "userPosts", "newsFeed"}, allEntries = true)
    public Post createPost(UUID userId, String content, String visibility) {
        Post post = new Post.Builder()
                .userId(userId)
                .content(content)
                .visibility(visibility)
                .build();
        return postRepository.save(post);
    }

    /** Get news feed for a user (cached by userId) */
    //@Cacheable(value = "newsFeed", key = "#userId")
    public List<Post> getNewsFeedForUser(UUID userId) {
        try {

            List<UUID> followings = userClient.getFollowings(userId);
            System.out.println("Followings for user " + userId + ": " + followings);

            if (followings == null || followings.isEmpty()) {
                return Collections.emptyList();
            }

            List<Post> posts = postRepository.findByUserIdIn(followings);
            System.out.println("Posts found: " + posts);

            return posts;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /** Get all public posts */
    public List<Post> getAllPublicPosts() {
        System.out.println("service");
        return postRepository.findByVisibility("public");
    }

    /** Get posts by a specific user */
    public List<Post> getPostsByUser(UUID userId) {
        return postRepository.findByUserId(userId);
    }

    /** Update a post only if the user owns it */
    @CacheEvict(value = {"publicPosts", "userPosts", "newsFeed"}, allEntries = true)
    public Post updatePost(String postId, UUID userId, String content, String visibility) {
        Optional<Post> optionalPost = postRepository.findById(postId)
                .filter(post -> post.getUserId().equals(userId));

        if (optionalPost.isEmpty()) {
            throw new IllegalStateException("User does not own this post or post does not exist.");
        }

        Post post = optionalPost.get();
        post.setContent(content);
        post.setVisibility(visibility);

        return postRepository.save(post);
    }

    /** Delete a post only if the user owns it */
    @CacheEvict(value = {"publicPosts", "userPosts", "newsFeed"}, allEntries = true)
    public void deletePost(String postId, UUID userId) {
        Optional<Post> optionalPost = postRepository.findById(postId)
                .filter(post -> post.getUserId().equals(userId));

        if (optionalPost.isEmpty()) {
            throw new IllegalStateException("User does not own this post or post does not exist.");
        }

        postRepository.deleteById(postId);
    }
}
