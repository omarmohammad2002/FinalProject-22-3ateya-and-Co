package com.example.anghamna.StreamingService.Services;
import com.example.anghamna.StreamingService.Commands.*;

import com.example.anghamna.StreamingService.Models.Audio;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.anghamna.StreamingService.Repositories.AudioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.UUID;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class StreamService {

    @Value("${media.storage.path}")
    private String storagePath;

    private final AudioRepository audioRepository;
    private static final Logger logger = LoggerFactory.getLogger(StreamService.class);


    public StreamService(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
    }


    public ResponseEntity<InputStreamResource> execute(UUID songId, String rangeHeader, Boolean bool) throws Exception {
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
                if (rangeStart == 0 && bool) {
                    File adFile = new File(storagePath + File.separator + "f47ac10b-01cc-4372-a567-0e02b2c3d479");
                    File tempFile = File.createTempFile("stream_with_ad_", ".wav");

                    concatenateWavFiles(adFile, audioFile, tempFile);  // Correct order: ad first

                    audioFile = tempFile; // Use the temp concatenated file for streaming
                    fileSize = tempFile.length(); // Update file size for headers
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
        if (bool) {
            File adFile = new File(storagePath + File.separator + "f47ac10b-01cc-4372-a567-0e02b2c3d479");
            File tempFile = File.createTempFile("stream_with_ad_", ".wav");

            concatenateWavFiles(adFile, audioFile, tempFile);
            audioFile = tempFile;
            fileSize = tempFile.length();
        }
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


    public String getAudioIdBySongId(UUID songId) throws FileNotFoundException {
        Audio audio = audioRepository.findBySongId(songId)
                .orElseThrow(() -> new FileNotFoundException("Audio not found for songId: " + songId));
        logger.info("✅ Found Audio document, internal Mongo _id: {}", audio.getId());
        return audio.getId();
    }

    public void concatenateWavFiles(File wav1, File wav2, File output) throws Exception {
        AudioInputStream clip1 = AudioSystem.getAudioInputStream(wav1);
        AudioInputStream clip2 = AudioSystem.getAudioInputStream(wav2);

        AudioInputStream appendedFiles =
                new AudioInputStream(
                        new SequenceInputStream(clip1, clip2),
                        clip1.getFormat(),
                        clip1.getFrameLength() + clip2.getFrameLength());

        AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, output);
    }

}
