package com.example.anghamna.StreamingService.Services;
import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;

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

    @Value("${media.storage.path}")
    private String storagePath;

    private static final Logger logger = LoggerFactory.getLogger(AudioService.class);

    public AudioService(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
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

    public String getAudioIdBySongId(UUID songId) throws FileNotFoundException {
        Audio audio = audioRepository.findBySongId(songId)
            .orElseThrow(() -> new FileNotFoundException("Audio not found for songId: " + songId));
        logger.info("✅ Found Audio document, internal Mongo _id: {}", audio.getId());
        return audio.getId();
    }
    
    public InputStream streamAudio(String audioId) throws IOException {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new FileNotFoundException("Audio not found"));

        File audioFile = new File(audio.getFilePath());
        if (!audioFile.exists()) {
            throw new FileNotFoundException("Audio file not found on server.");
        }

        return new FileInputStream(audioFile);
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

    public List<Audio> getAllAudio() {
        return audioRepository.findAll() ;
    }

    public ResponseEntity<InputStreamResource> streamAudio(UUID songId, String rangeHeader) throws IOException {
        logger.info("🔊 [START] streamAudio request");
        logger.info("➡️ Received songId: {}", songId);
        logger.info("➡️ Received rangeHeader: {}", rangeHeader);
    
        String audioId = getAudioIdBySongId(songId);
        logger.info("✅ Retrieved audioId from DB: {}", audioId);
    
        File audioFile = getAudioFile(audioId);
        logger.info("📁 Audio file path resolved: {}", audioFile.getAbsolutePath());
    
        long fileSize = audioFile.length();
        logger.info("📏 Total file size: {} bytes", fileSize);
    
        InputStream inputStream;
        HttpHeaders responseHeaders = new HttpHeaders();
    
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            logger.info("📡 Range request detected");
    
            Pattern pattern = Pattern.compile("bytes=(\\d+)-(\\d*)");
            Matcher matcher = pattern.matcher(rangeHeader);
    
            if (matcher.matches()) {
                String startStr = matcher.group(1);
                String endStr = matcher.group(2);
                logger.info("🔍 Parsed range: start={} end={}", startStr, endStr.isEmpty() ? "[empty]" : endStr);
    
                long rangeStart = Long.parseLong(startStr);
                long rangeEnd = endStr.isEmpty() ? fileSize - 1 : Long.parseLong(endStr);
    
                if (rangeStart > rangeEnd || rangeEnd >= fileSize) {
                    logger.warn("❌ Invalid byte range: {} - {} out of {}", rangeStart, rangeEnd, fileSize);
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
                }
    
                long contentLength = rangeEnd - rangeStart + 1;
                logger.info("📤 Serving partial content: {} bytes ({} - {})", contentLength, rangeStart, rangeEnd);
    
                inputStream = new FileInputStream(audioFile);
                long skipped = inputStream.skip(rangeStart);
                logger.info("↪️ Skipped {} bytes to start streaming from byte {}", skipped, rangeStart);
    
                responseHeaders.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
                responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
                responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
                responseHeaders.set(HttpHeaders.CONTENT_RANGE,
                        String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));
    
                logger.info("✅ Returning 206 Partial Content");
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(responseHeaders)
                        .body(new InputStreamResource(inputStream));
            } else {
                logger.warn("❌ Malformed range header: {}", rangeHeader);
            }
        }
    
        // No valid range header — return full file
        logger.info("⚠️ No range or invalid range header. Streaming full file.");
    
        inputStream = new FileInputStream(audioFile);
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
        responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
    
        logger.info("✅ Returning 200 OK with full file stream");
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(new InputStreamResource(inputStream));
    }
    

    public File getAudioFile(String audioId) throws IOException {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new FileNotFoundException("Audio not found in DB"));
        File audioFile = new File(audio.getFilePath());
        if (!audioFile.exists()) {
            throw new FileNotFoundException("Audio file not found on disk");
        }
        return audioFile;
    }
}
