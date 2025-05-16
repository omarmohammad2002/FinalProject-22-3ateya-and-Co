package com.example.anghamna.MusicService.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    private String title;

//    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    private String genre;

    private int duration; // in seconds

    @CreationTimestamp
//    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
//    @Column(name = "updated_at")
    private Date updatedAt;

    private int likeCount = 0;

    private int streamCount = 0;

    @JsonBackReference
    @JsonIgnore
    @ManyToMany(mappedBy = "all_songs", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Playlist> playlists = new HashSet<>();


    // when they call upload song we call on both create song in both services?
    // or should this service send it to sttreaming and pass the strong id when we create it
// we need to add the songURL field to the constructor and getters/setters
    //Instant now = Instant.now();
    //        created_at = Date.from(now);
    
    // Constructors
    public Song() {}

    public Song(String title, String genre, int duration) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.likeCount = 0;
        this.streamCount = 0;
        this.createdAt = Date.from(Instant.now());
        this.updatedAt = Date.from(Instant.now());

    }


    public Song(String title, UUID artistId, String genre, int duration, int likeCount, int streamCount) {
        this.title = title;
        this.artistId = artistId;
        this.genre = genre;
        this.duration = duration;
        this.likeCount = likeCount;
        this.streamCount = streamCount;
        this.createdAt = Date.from(Instant.now());
        this.updatedAt = Date.from(Instant.now());

    }

    public Song(UUID id, String title, UUID artistId, String genre, int duration, Date createdAt, int likeCount, int streamCount) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
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

    public Set<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        this.playlists = playlists;
    }
}
