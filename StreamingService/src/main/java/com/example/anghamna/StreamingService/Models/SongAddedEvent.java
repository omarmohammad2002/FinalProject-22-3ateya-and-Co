package com.example.anghamna.StreamingService.Models;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class SongAddedEvent {
    private UUID songId;
    private MultipartFile file;

    // Getters and setters
    public UUID getSongId() { return songId; }
    public void setSongId(UUID songId) { this.songId = songId; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
