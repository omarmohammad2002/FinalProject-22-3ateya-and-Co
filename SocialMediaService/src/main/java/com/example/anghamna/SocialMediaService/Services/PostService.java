package com.example.anghamna.SocialMediaService.Services;

import com.example.anghamna.SocialMediaService.Clients.UserClient;
import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Cacheable(value = "newsFeed", key = "#userId")
    public List<Post> getNewsFeedForUser(UUID userId) {
        List<String> followings = userClient.getFollowings(userId);
        return postRepository.findByUserIdIn(followings);
    }

    /** Get all public posts (cached globally) */

    public List<Post> getAllPublicPosts() {
        System.out.println("service");
        return postRepository.findByVisibility("public");
    }

    /** Get posts by a user (cached by userId) */

    public List<Post> getPostsByUser(UUID userId) {
        return postRepository.findByUserId(userId);
    }

    /** Update a post (Evict affected user's cache and public posts) */
    @CacheEvict(value = {"publicPosts", "userPosts", "newsFeed"}, allEntries = true)
    public Post updatePost(Post updated) {
        return postRepository.save(updated);
    }

    /** Delete a post and evict all related caches */
    @CacheEvict(value = {"publicPosts", "userPosts", "newsFeed"}, allEntries = true)
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }
}
