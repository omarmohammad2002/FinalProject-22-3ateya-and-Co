package com.example.anghamna.SocialMediaService.Repositories;


import com.example.anghamna.SocialMediaService.Models.Feed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends MongoRepository<Feed, String> {

    List<Feed> findByUserIdOrderByTimestampDesc(String userId);
}
