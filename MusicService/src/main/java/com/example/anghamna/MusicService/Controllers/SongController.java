package com.spotify.music.controller;

import com.spotify.music.model.Song;
import com.spotify.music.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    // Create a new song
    @PostMapping
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song) {
        Song created = songService.createSong(song);
        return ResponseEntity.status(201).body(created);
    }

    // Get all songs
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // Get song by ID
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable UUID id) {
        return songService.getSongById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get songs by artist
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Song>> getSongsByArtist(@PathVariable UUID artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    // Get songs by genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Song>> getSongsByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(songService.getSongsByGenre(genre));
    }

    // Search songs by title (partial match)
    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongsByTitle(@RequestParam("title") String title) {
        return ResponseEntity.ok(songService.searchSongsByTitle(title));
    }

    // Get top streamed songs
    @GetMapping("/top-streamed")
    public ResponseEntity<List<Song>> getTopStreamedSongs(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopStreamedSongs(limit));
    }

    // Get a random song (discover mode)
    @GetMapping("/random")
    public ResponseEntity<Song> getRandomSong() {
        return songService.getRandomSong()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Update song
    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable UUID id, @Valid @RequestBody Song updatedSong) {
        return songService.updateSong(id, updatedSong)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete song
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable UUID id) {
        if (songService.deleteSong(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
