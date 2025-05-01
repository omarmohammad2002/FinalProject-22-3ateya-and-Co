package com.example.anghamna.StreamingService.Services;
import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AudioService {

    private final AudioRepository audioRepository;

    @Value("${media.storage.path}")
    private String storagePath;

    @Autowired
    public AudioService(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
    }

    public Audio uploadAudio(UUID songId, MultipartFile file) throws IOException {
        String filename = songId.toString() ;
        File dest = new File(storagePath + File.separator + filename);
        file.transferTo(dest);

        Audio audio = new Audio(songId, dest.getAbsolutePath(), LocalDateTime.now());
        return audioRepository.save(audio);
    }
    public String getAudioIdBySongId(UUID songId)
    {
        return audioRepository.getAudioIdBySongId(songId) ;
    }
    // Method to stream audio
    public InputStream streamAudio(String audioId) throws IOException {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new FileNotFoundException("Audio not found"));

        File audioFile = new File(audio.getFilePath());
        if (!audioFile.exists()) {
            throw new FileNotFoundException("Audio file not found on server.");
        }

        return new FileInputStream(audioFile);  // Return an InputStream to the file
    }

    public long getAudioFileSize(String audioId) throws IOException {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new FileNotFoundException("Audio not found"));

        File audioFile = new File(audio.getFilePath());
        if (!audioFile.exists()) {
            throw new FileNotFoundException("Audio file not found.");
        }

        return audioFile.length();
    }
}
