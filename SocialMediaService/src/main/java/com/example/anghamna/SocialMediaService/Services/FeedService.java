package com.example.anghamna.SocialMediaService.Services;


import com.example.anghamna.SocialMediaService.Models.Feed;
import com.example.anghamna.SocialMediaService.Repositories.FeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FeedService {

    private FeedRepository feedRepository;

    @Autowired
    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public Feed createFeed(Feed feed) {
        feed.setTimestamp(new Date());
        return feedRepository.save(feed);
    }

    @Cacheable(value = "userFeeds", key = "#userId")
    public List<Feed> getFeedsForUser(String userId) {
        return feedRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
