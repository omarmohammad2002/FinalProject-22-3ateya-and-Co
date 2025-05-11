package com.spotify.music.service;

import com.spotify.music.dto.PlaylistRequest;
import com.spotify.music.dto.PlaylistResponse;
import com.spotify.music.exception.*;
import com.spotify.music.model.Playlist;
import com.spotify.music.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    // CREATE
    @Transactional
    public PlaylistResponse createPlaylist(PlaylistRequest request, Long ownerId) {
        if (playlistRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new DuplicateResourceException("Playlist name already exists");
        }

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .ownerId(ownerId)
                .isPrivate(request.isPrivate())
                .build();

        return mapToResponse(playlistRepository.save(playlist));
    }

    // READ (Single)
    public PlaylistResponse getPlaylist(Long id, Long requestingUserId) {
        return playlistRepository.findById(id)
                .filter(p -> !p.isPrivate() || p.getOwnerId().equals(requestingUserId))
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found or private"));
    }

    // READ (All for user)
    public List<PlaylistResponse> getUserPlaylists(Long ownerId) {
        return playlistRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // READ (Public)
    public List<PlaylistResponse> getPublicPlaylists() {
        return playlistRepository.findByIsPrivateFalse().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE
    @Transactional
    public PlaylistResponse updatePlaylist(Long id, PlaylistRequest request, Long ownerId) {
        return playlistRepository.findByIdAndOwnerId(id, ownerId)
                .map(existing -> {
                    if (!existing.getName().equals(request.getName()) {
                        if (playlistRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
                            throw new DuplicateResourceException("Playlist name already exists");
                        }
                        existing.setName(request.getName());
                    }
                    existing.setPrivate(request.isPrivate());
                    return mapToResponse(playlistRepository.save(existing));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
    }

    // DELETE
    @Transactional
    public void deletePlaylist(Long id, Long ownerId) {
        if (!playlistRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new ResourceNotFoundException("Playlist not found");
        }
        playlistRepository.deleteById(id);
    }

    // Toggle privacy
    @Transactional
    public void togglePrivacy(Long id, Long ownerId) {
        Playlist playlist = playlistRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        playlist.setPrivate(!playlist.isPrivate());
        playlistRepository.save(playlist);
    }

    // Mapper
    private PlaylistResponse mapToResponse(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .ownerId(playlist.getOwnerId())
                .isPrivate(playlist.isPrivate())
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .songCount(playlist.getSongs().size())
                .build();
    }
}