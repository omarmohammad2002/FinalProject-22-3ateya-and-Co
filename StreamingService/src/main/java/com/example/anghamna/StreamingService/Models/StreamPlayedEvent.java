package com.example.anghamna.StreamingService.Models;

import java.util.UUID;

public class StreamPlayedEvent {
    private UUID songId;


    // Getters and setters
    public UUID getSongId() { return songId; }
    public void setSongId(UUID songId) { this.songId = songId; }
}
