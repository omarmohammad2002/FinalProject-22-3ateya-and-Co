package com.example.anghamna.MusicService.Controllers;

//import com.spotify.music.dto.PlaylistRequest;
//import com.spotify.music.dto.PlaylistResponse;
import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.Services.PlaylistService;
import com.example.anghamna.MusicService.command.AddSongCommand;
import com.example.anghamna.MusicService.command.PlaylistCommand;
import com.example.anghamna.MusicService.command.RemoveSongCommand;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/playlists")
//@RequiredArgsConstructor
public class PlaylistController {

    @Autowired //we added those 2 for command pattern to add/remove song
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
    public ResponseEntity<Playlist> createPlaylist(
            @Valid @RequestBody Playlist request)
          //  @RequestHeader("X-User-ID") Long userId)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistService.createPlaylist(request/*, userId*/));
    }

    // Get Playlist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable UUID id) {
        return playlistService.getPlaylistById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Get user playlists

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(playlistService.getPlaylistsByUserId(userId));
    }


    @GetMapping("/public")
    public ResponseEntity<List<Playlist>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }




    //FIXME need to ensure its the user who created the playlist
    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable UUID id, @RequestBody() Playlist playlist){
        return playlistService.updatePlaylist(id, playlist)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }






    //   FIXME check who owns it, only user can delete it, how do we take it so we can pass it
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable UUID id,
              @RequestHeader() UUID userId) { //FIXME is this how itll be passed
        {
            playlistService.deletePlaylist(id, userId);
            return ResponseEntity.noContent().build();
        }
    }

    //FIXME check who owns it and only they can toggle privacy
    @PatchMapping("privacy/{id}/{userId}")
    public void togglePrivacy(
            @PathVariable UUID id,
            @PathVariable() UUID userId) { //FIXME is this how itll be passed
        playlistService.togglePrivacy(id, userId);
    }

    @PostMapping("/{playlistId}/add")
    public void addSong(@PathVariable UUID playlistId, @RequestBody() UUID songId) {
        AddSongCommand addCommand = new AddSongCommand(playlistRepository,songRepository, playlistId, songId);
        playlistService.addSong(addCommand);
    }

    @DeleteMapping("/{playlistId}/remove")
    public void removeSong(@PathVariable UUID playlistId, @RequestBody() UUID songId) {
        RemoveSongCommand removeCommand = new RemoveSongCommand(playlistRepository, songRepository, playlistId, songId);
        playlistService.removeSong(removeCommand);
    }

}