package com.example.anghamna.MusicService.Controllers;


import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.Services.PlaylistService;
import com.example.anghamna.MusicService.command.AddSongCommand;
import com.example.anghamna.MusicService.command.RemoveSongCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    //Create a playlist
    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist, @CookieValue("USER_ID") String userIdCookie)
    {
        UUID userId = UUID.fromString(userIdCookie);
        playlist.setOwnerId(userId);
        Playlist created = playlistService.createPlaylist(playlist);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable UUID id, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        if (playlist.isPrivate() && !playlist.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Playlist result = playlistService.getPlaylistById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found in service"));

        return ResponseEntity.ok(result);
    }


    // Get user playlists

    @GetMapping("/user/{id}")
    public ResponseEntity<Set<Playlist>> getUserPlaylists(@PathVariable("id") UUID userId, @CookieValue("USER_ID") String userIdCookie) {
        UUID id = UUID.fromString(userIdCookie);
        if(userId.equals(id)){
            return ResponseEntity.ok(playlistService.getPlaylistsByUserId(userId));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @GetMapping("/public")
    public ResponseEntity<Set<Playlist>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }

    @GetMapping("/playlistSongs/{playlistId}")
    public ResponseEntity<Set<Song>> getPlaylistSongs(@PathVariable UUID playlistId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
        if (playlist.isPrivate() && !playlist.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Set<Song> songs = playlistService.getPlaylistSongs(playlistId);
        return ResponseEntity.ok(songs);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable UUID id, @RequestBody() Playlist playlist,
                                                   @CookieValue("USER_ID") String userIdCookie){
        UUID userId = UUID.fromString(userIdCookie);
        if(playlist.getOwnerId().equals(userId)){
            return playlistService.updatePlaylist(id, playlist)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable UUID playlistId, @CookieValue("USER_ID") String userIdCookie) {
            UUID userId = UUID.fromString(userIdCookie);
            Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
            if(playlist.getOwnerId().equals(userId)){
                playlistService.deletePlaylist(playlistId, userId);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @PatchMapping("/privacy/{playlistId}")
    public void togglePrivacy(@PathVariable UUID playlistId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
        if(playlist.getOwnerId().equals(userId)){
            playlistService.togglePrivacy(playlistId, userId);
        }
    }

    @PostMapping("/{playlistId}/add")
    public void addSong(@PathVariable UUID playlistId, @RequestBody() UUID songId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
        if(playlist.getOwnerId().equals(userId)){
            AddSongCommand addCommand = new AddSongCommand(playlistRepository,songRepository, playlistId, songId);
            playlistService.addSong(addCommand);
        }
    }

    @DeleteMapping("/{playlistId}/remove")
    public void removeSong(@PathVariable UUID playlistId, @RequestBody() UUID songId, @CookieValue("USER_ID") String userIdCookie) {
        UUID userId = UUID.fromString(userIdCookie);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
        if(playlist.getOwnerId().equals(userId)) {
            RemoveSongCommand removeCommand = new RemoveSongCommand(playlistRepository, songRepository, playlistId, songId);
            playlistService.removeSong(removeCommand);
        }
    }

}