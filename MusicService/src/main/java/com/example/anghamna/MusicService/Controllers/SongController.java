package com.example.anghamna.MusicService.Controllers;


import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Services.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private final SongService songService;



    public SongController(SongService songService) {
        this.songService = songService;
    }


    // Create a new song
    @PostMapping("/")
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song, @CookieValue("USER_ID") String userIdCookie) {
        UUID id = UUID.fromString(userIdCookie);
        song.setArtistId(id);
        Song created = songService.createSong(song);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    // Get all songs
    @GetMapping("/")
    public ResponseEntity<Set<Song>> getAllSongs() {
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
    public ResponseEntity<Set<Song>> getSongsByArtist(@PathVariable UUID artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    // Get songs by genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Set<Song>> getSongsByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(songService.getSongsByGenre(genre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable UUID id, @Valid @RequestBody Song updatedSong,
                                           @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        if(updatedSong.getArtistId().equals(userId)) {
            songService.updateSong(id, updatedSong);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedSong);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PutMapping("/{id}/like")
    public ResponseEntity<Song> updateSongLikeCount(@PathVariable UUID id) {
        if (songService.likedSong(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

   @PutMapping("/{id}/stream")
   public void updateSongStreamCount(@PathVariable String id) {
       songService.streamedSong(id);
   }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> deleteSong(@PathVariable UUID songId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Optional<Song> optionalSong = songService.getSongById(songId);
        if (optionalSong.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Song song = optionalSong.get();
        if(song.getArtistId().equals(userId)) {
            if (songService.deleteSong(songId)) {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    //Delete song by artist

    @DeleteMapping("/artist/{artistId}")
    public void deleteSongsByArtist(@PathVariable String artistId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        UUID artistIdTransformed = UUID.fromString(artistId);
        if(artistIdTransformed.equals(userId)) {
            songService.deleteSongsByArtist(artistId);
        }

    }

}
