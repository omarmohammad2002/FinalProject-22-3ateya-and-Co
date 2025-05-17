
package com.example.anghamna.MusicService.Models;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    private String name;

    private UUID ownerId;

    private boolean isPrivate = true;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "playlist_songs",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> all_songs = new HashSet<>();

    // Constructors
    public Playlist() {
        this.all_songs = new HashSet<>();
    }

    public Playlist(String name, boolean isPrivate, Song song) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.createdAt = Date.from(Instant.now());
        this.all_songs.add(song);
        this.updatedAt = Date.from(Instant.now());


    }

    public Playlist(String name, UUID ownerId, boolean isPrivate, Set<Song> songs) {
        this.name = name;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.createdAt = Date.from(Instant.now());
        this.all_songs = songs;
        this.updatedAt = Date.from(Instant.now());

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Song> getSongs() {
        return all_songs;
    }

    public void setSongs(Set<Song> songs) {
        this.all_songs = (songs != null) ? songs : new HashSet<>();
    }

}
