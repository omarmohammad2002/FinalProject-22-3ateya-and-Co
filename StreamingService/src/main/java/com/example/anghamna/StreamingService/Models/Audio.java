package com.example.anghamna.StreamingService.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "audio")
public class Audio {

    @Id
    private String id;
    private UUID songId; //check with music microservice
    private String filePath;

    public Audio() {}

    public Audio(UUID songId, String filePath) {
        this.songId = songId;
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public UUID getSongId() {
        return songId;
    }

    public void setSongId(UUID songId) {
        this.songId = songId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
