package com.example.anghamna.StreamingService.Repositories;

import com.example.anghamna.StreamingService.Models.Audio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface AudioRepository extends MongoRepository<Audio, String> {
    Optional<Audio> findBySongId(UUID songId);
}
