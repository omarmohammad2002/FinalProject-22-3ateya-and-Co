package com.example.anghamna.StreamingService.Controllers;
import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Services.AudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/streaming")
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

    @DeleteMapping("/delete/{songId}")
    public String deleteAudio(@PathVariable UUID songId) throws IOException {
        return audioService.deleteAudio(songId);
    }


    @GetMapping("/stream/{songId}")
    public ResponseEntity<InputStreamResource> streamAudio(
            @PathVariable UUID songId,
            @RequestParam("userType") String userType,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader) throws Exception {
        return audioService.streamAudioController(songId, rangeHeader, userType);
    }    

}
