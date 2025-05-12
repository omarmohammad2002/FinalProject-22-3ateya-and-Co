package com.example.anghamna.MusicService.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    private String title;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    private String genre;

    private int duration; // in seconds

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private int likeCount = 0;

    private int streamCount = 0;

    @ManyToMany(mappedBy = "songs")
    private List<Playlist> playlists;

    //FIXME revise with omar if we need it, we need to store the song url or have user?
    private String songURL;
    // when they call upload song we call on both create song in both services?
    // or should this service send it to sttreaming and pass the strong id when we create it
// we need to add the songURL field to the constructor and getters/setters

    
    // Constructors
    public Song() {}

    public Song(String title, UUID artistId, String genre, int duration) {
        this.title = title;
        this.artistId = artistId;
        this.genre = genre;
        this.duration = duration;
        this.likeCount = 0;
        this.streamCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public Song(String title, UUID artistId, String genre, int duration, int likeCount, int streamCount) {
        this.title = title;
        this.artistId = artistId;
        this.genre = genre;
        this.duration = duration;
        this.likeCount = likeCount;
        this.streamCount = streamCount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    public Song(UUID id, String title, UUID artistId, String genre, int duration, LocalDateTime createdAt, int likeCount, int streamCount) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.genre = genre;
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.likeCount = likeCount;
        this.streamCount = streamCount;
    }



    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getArtistId() {
        return artistId;
    }

    public void setArtistId(UUID artistId) {
        this.artistId = artistId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getStreamCount() {
        return streamCount;
    }

    public void setStreamCount(int streamCount) {
        this.streamCount = streamCount;
    }

}
