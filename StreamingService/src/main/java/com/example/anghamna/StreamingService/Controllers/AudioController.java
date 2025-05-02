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

    @Autowired
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
            @RequestHeader HttpHeaders headers) throws IOException {
        try {
            logger.info("Attempting to stream audio for songId: {}", songId);
            
            // Get audio file and file size
            String audioId = audioService.getAudioIdBySongId(songId);
            logger.info("Found audioId: {}", audioId);
            
            InputStream inputStream = audioService.streamAudio(audioId);
            logger.info("Successfully opened input stream");
            
            long fileSize = audioService.getAudioFileSize(audioId);
            logger.info("File size: {} bytes", fileSize);

            // Handle Range Header for partial content
            String rangeHeader = headers.getFirst(HttpHeaders.RANGE);
            if (rangeHeader != null) {
                logger.info("Range header received: {}", rangeHeader);
                Pattern pattern = Pattern.compile("bytes=(\\d+)-(\\d*)");
                Matcher matcher = pattern.matcher(rangeHeader);

                if (matcher.matches()) {
                    long rangeStart = Long.parseLong(matcher.group(1));
                    long rangeEnd = matcher.group(2).isEmpty() ? fileSize - 1 : Long.parseLong(matcher.group(2));
                    logger.info("Range: {} to {}", rangeStart, rangeEnd);

                    long contentLength = rangeEnd - rangeStart + 1;
                    inputStream.skip(rangeStart);

                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
                    responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
                    responseHeaders.set(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
                    responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .headers(responseHeaders)
                            .body(new InputStreamResource(inputStream));
                }
            }

            // Default: return the full file if no range is provided
            HttpHeaders headersForFullContent = new HttpHeaders();
            headersForFullContent.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
            headersForFullContent.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));

            return ResponseEntity.ok()
                    .headers(headersForFullContent)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            logger.error("Error streaming audio for songId: " + songId, e);
            throw e;
        }
    }
}
