package com.example.anghamna.StreamingService.Controllers;

import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Services.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/audio")
public class AudioController {
    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);
    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/getAll")
    public List<Audio> getAllAudio() {
        return audioService.getAllAudio();
    }

    @PostMapping("/upload")
    public ResponseEntity<Audio> uploadAudio(@RequestParam("songId") UUID songId,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        Audio uploaded = audioService.uploadAudio(songId, file);
        return ResponseEntity.ok(uploaded);
    }


    @GetMapping("/stream/{songId}")
    public ResponseEntity<InputStreamResource> streamAudio(
            @PathVariable UUID songId,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) throws IOException {
        return audioService.streamAudio(songId, rangeHeader);
    }    

}
