package com.example.anghamna.StreamingService.Services;

import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Service
public class AudioLookupService {
    private final AudioRepository audioRepository;

    public AudioLookupService(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
    }

    @Cacheable(value = "audio", key = "#songId")
    public Audio getAudioIdBySongId(UUID songId) throws FileNotFoundException {
        return audioRepository.findBySongId(songId)
                .orElseThrow(() -> new FileNotFoundException("Audio not found for songId: " + songId));
    }
    public File getAudioFile(Audio audio) throws IOException {
        File audioFile = new File(audio.getFilePath());
        if (!audioFile.exists()) {
            throw new FileNotFoundException("Audio file not found on disk");
        }
        return audioFile;
    }
}
