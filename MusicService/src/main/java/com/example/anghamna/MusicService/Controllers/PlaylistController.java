package com.spotify.music.controller;

import com.spotify.music.dto.PlaylistRequest;
import com.spotify.music.dto.PlaylistResponse;
import com.spotify.music.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @Valid @RequestBody PlaylistRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistService.createPlaylist(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylist(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = false) Long userId) {
        return ResponseEntity.ok(playlistService.getPlaylist(id, userId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylists(
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(playlistService.getUserPlaylists(userId));
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlaylistResponse>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(
            @PathVariable Long id,
            @Valid @RequestBody PlaylistRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {
        playlistService.deletePlaylist(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/privacy")
    public ResponseEntity<Void> togglePrivacy(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {
        playlistService.togglePrivacy(id, userId);
        return ResponseEntity.noContent().build();
    }
}