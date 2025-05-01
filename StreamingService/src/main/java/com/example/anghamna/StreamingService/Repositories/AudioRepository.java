package com.example.anghamna.StreamingService.Repositories;
import com.example.anghamna.StreamingService.Models.Audio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface AudioRepository extends MongoRepository<Audio, String> {

    public String getAudioIdBySongId (UUID songId);

}
