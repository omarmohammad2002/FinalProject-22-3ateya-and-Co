package com.example.anghamna.SocialMediaService.configs;

import com.example.anghamna.SocialMediaService.Models.Post;
import com.example.anghamna.SocialMediaService.Models.Comment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(90)) // Default TTL for all caches
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<>(Object.class)));

        // Custom configurations for different entities
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache configuration for Students
        cacheConfigurations.put("posts",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer())));
        // Cache configuration for Students
        cacheConfigurations.put("public",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer())));
        // Cache configuration for Students
        cacheConfigurations.put("commentsByPost",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer())));
        // Cache configuration for Students
        cacheConfigurations.put("commentsById",
                defaultConfig.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer())));


        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig) // Default settings
                .withInitialCacheConfigurations(cacheConfigurations) // Custom per-cache configurations
                .build();
    }
}
