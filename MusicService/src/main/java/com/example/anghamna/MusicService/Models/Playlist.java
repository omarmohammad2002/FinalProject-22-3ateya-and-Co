
package com.example.anghamna.MusicService.Models;

import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "playlists")
public class Playlist {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    //FIXME do we need to keep this as @Column?
    @Column(name = "is_private")
    private boolean isPrivate = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    @ManyToMany
    @JoinTable(
            name = "playlist_songs",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs ;

    // Constructors
    public Playlist() {}

    public Playlist(String name, UUID ownerId, boolean isPrivate, Song song) {
        this.name = name;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.createdAt = LocalDateTime.now();
        this.songs.add(song);
         this.updatedAt = LocalDateTime.now();

    }

    public Playlist(String name, UUID ownerId, boolean isPrivate, List<Song> songs) {
        this.name = name;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.createdAt = LocalDateTime.now();
        this.songs = songs;
       this.updatedAt = LocalDateTime.now();

    }



    public Playlist(String name, UUID ownerId, boolean isPrivate, LocalDateTime createdAt) {
        this.name = name;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;

    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }





    //FIXMe implement add song in playlist and remove.. when adding
    // we have to choose the playlist we want to add the song to
//    // Helper methods
//    public void addSong(Song song) {
//        this.songs.add(song);
//        song.getPlaylists().add(this);
//    }
//
//    public void removeSong(Song song) {
//        this.songs.remove(song);
//        song.getPlaylists().remove(this);
//    }
}
