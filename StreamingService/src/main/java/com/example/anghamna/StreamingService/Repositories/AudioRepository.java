package com.example.anghamna.StreamingService.Repositories;
import com.example.anghamna.StreamingService.Models.Audio;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AudioRepository extends MongoRepository<Audio, String> {

    public String getAudioIdBySongId(UUID songId);

}
