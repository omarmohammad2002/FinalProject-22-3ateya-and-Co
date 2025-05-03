package com.example.anghamna.StreamingService.Services;
import com.example.anghamna.StreamingService.Commands.AudioStreamingCommand;
import com.example.anghamna.StreamingService.Commands.CommandInvoker;
import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;
import com.example.anghamna.StreamingService.Commands.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AudioService {

    private final AudioRepository audioRepository;
    private final CommandInvoker streamingCommandInvoker;

    @Value("${media.storage.path}")
    private String storagePath;

    private static final Logger logger = LoggerFactory.getLogger(AudioService.class);



    public AudioService(AudioRepository audioRepository, CommandInvoker streamingCommandInvoker) {
        this.audioRepository = audioRepository;
        this.streamingCommandInvoker = streamingCommandInvoker;
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

            Audio audio = new Audio(songId, dest.getAbsolutePath(), LocalDateTime.now());
            return audioRepository.save(audio);
        } catch (Exception e) {
            throw new IOException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public List<Audio> getAllAudio() {
        return audioRepository.findAll();
    }

    public ResponseEntity<InputStreamResource> streamAudioController(UUID songId, String rangeHeader, String userType) throws Exception {
        AudioStreamingCommand command = streamingCommandInvoker.getCommand(userType);
        return command.execute(songId, rangeHeader);
    }

}

