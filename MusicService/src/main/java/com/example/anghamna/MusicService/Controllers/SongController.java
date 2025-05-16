package com.example.anghamna.MusicService.Controllers;


import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Services.PlaylistService;
import com.example.anghamna.MusicService.Services.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song) {
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


    @GetMapping("/getPlaylists/{songId}")
    public Set<Playlist> getPlaylistsBySongId(@PathVariable UUID songId) {
        return songService.getSongById(songId).get().getPlaylists();
    }

//    // Search songs by title (partial match)
//    @GetMapping("/search")
//    public ResponseEntity<List<Song>> searchSongsByTitle(@RequestParam("title") String title) {
//        return ResponseEntity.ok(songService.searchSongsByTitle(title));
//    }

//    // Get top streamed songs
//    @GetMapping("/top-streamed")
//    public ResponseEntity<List<Song>> getTopStreamedSongs(@RequestParam(defaultValue = "10") int limit) {
//        return ResponseEntity.ok(songService.getTopStreamedSongs(limit));
//    }

//    // Get a random song (discover mode)
//    @GetMapping("/random")
//    public ResponseEntity<Song> getRandomSong() {
//        return songService.getRandomSong()
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.noContent().build());
//    }


    //FIXME ensure the artist is the user
    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable UUID id, @Valid @RequestBody Song updatedSong) {
        return songService.updateSong(id, updatedSong)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//FIXME ensure you can only like once as a user

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

//
//    @PutMapping("/{id}/name")
//    public ResponseEntity<Song> updateSongName(@PathVariable UUID id, @RequestParam String name) {
//        return songService.updateSongName(id, name)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }


  //  @PutMapping("/{id}/artist")
//    public ResponseEntity<Song> updateSongArtist(@PathVariable UUID id, @RequestParam UUID artistId) {
//        return songService.updateSongArtist(id, artistId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }


//FIXME the respone entities being returned?
    // Delete song

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable UUID id) {
        if (songService.deleteSong(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Delete song by artist

    @DeleteMapping("/artist/{artistId}")
    public void deleteSongsByArtist(@PathVariable UUID artistId) {
       songService.deleteSongsByArtist(artistId);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
    }

//    //Delete a specific song + ensure its the user thats the artist


//    @DeleteMapping("/artist/{artistId}/song/{songId}")
//    public ResponseEntity<Void> deleteSongByArtist(@RequestParam UUID artistId, @RequestParam UUID songId) {
//        if (songService.deleteSongByArtist(artistId, songId)) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
