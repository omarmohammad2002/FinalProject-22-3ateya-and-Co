package com.example.anghamna.StreamingService.Services;

import com.example.anghamna.StreamingService.Clients.UserClient;
import com.example.anghamna.StreamingService.Commands.AudioStreamingCommand;
import com.example.anghamna.StreamingService.Commands.CommandInvoker;
import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;
import com.example.anghamna.StreamingService.rabbitmq.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AudioService {

    private final AudioRepository audioRepository;
    private final CommandInvoker streamingCommandInvoker;
    private final AudioLookupService audioLookupService;

    @Autowired
    private UserClient userClient;

    @Value("${media.storage.path}")
    private String storagePath;

    public AudioService(AudioRepository audioRepository, CommandInvoker streamingCommandInvoker, AudioLookupService audioLookupService) {
        this.audioRepository = audioRepository;
        this.streamingCommandInvoker = streamingCommandInvoker;
        this.audioLookupService = audioLookupService;
    }

    @RabbitListener(queues = RabbitMQConfig.SONG_DELETED_QUEUE)
    public void handleSongDeleted(String message) throws IOException {
        UUID songId = UUID.fromString(message);
        System.out.println("Received request to delete song: " + songId);
        deleteAudio(songId);
    }

    public Audio uploadAudio(UUID songId, MultipartFile file) throws IOException {
        try {

            File directory = new File(storagePath);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory: " + storagePath);
                }
            }

            String filename = songId.toString();
            File dest = new File(storagePath + File.separator + filename);

            if (file.isEmpty()) {
                throw new IOException("Failed to store empty file");
            }

            file.transferTo(dest);

            Audio audio = new Audio(songId, dest.getAbsolutePath());
            return audioRepository.save(audio);
        } catch (Exception e) {
            throw new IOException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public List<Audio> getAllAudio() {
        return audioRepository.findAll();
    }

    public ResponseEntity<InputStreamResource> streamAudioController(UUID songId, String rangeHeader, long userID) throws Exception {
//        String userType = userClient.getUserTypeById(userID);
        String userType = "premium" ;
        AudioStreamingCommand command = streamingCommandInvoker.getCommand(userType);
        return command.execute(songId, rangeHeader);
    }

    @CacheEvict(value="audio", key = "#songId")
    public String deleteAudio(UUID songId) throws IOException {
        Audio audio = audioLookupService.getAudioIdBySongId(songId) ;
        File file = audioLookupService.getAudioFile(audio) ;
        if (file.delete()) {
            audioRepository.deleteById(audio.getId());
            return "Audio Deleted Successfully";
        }
        else {
            return "Audio Deletion Failed";
        }
    }




}

