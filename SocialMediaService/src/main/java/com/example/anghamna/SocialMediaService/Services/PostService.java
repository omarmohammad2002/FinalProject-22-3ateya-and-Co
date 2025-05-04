package com.example.anghamna.SocialMediaService.Services;


import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    /** Create a new Post via Builder and evict relevant caches */
    @CacheEvict(value = {"publicPosts", "userPosts"}, allEntries = true)
    public Post createPost(String userId, String content, String visibility) {
        Post post = new Post.Builder()
                .userId(userId)
                .content(content)
                .visibility(visibility)
                .build();
        return postRepository.save(post);
    }

    /** Get all public posts (cached) */
    @Cacheable("publicPosts")
    public List<Post> getAllPublicPosts() {
        return postRepository.findByVisibility("public");
    }

    /** Get posts by a user (cached) */
    @Cacheable(value = "userPosts", key = "#userId")
    public List<Post> getPostsByUser(String userId) {
        return postRepository.findByUserId(userId);
    }

    /** Update an existing post and evict caches */
    @CacheEvict(value = {"publicPosts", "userPosts"}, allEntries = true)
    public Post updatePost(Post updated) {
        return postRepository.save(updated);
    }

    /** Delete a post by ID and evict caches */
    @CacheEvict(value = {"publicPosts", "userPosts"}, allEntries = true)
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }
}
