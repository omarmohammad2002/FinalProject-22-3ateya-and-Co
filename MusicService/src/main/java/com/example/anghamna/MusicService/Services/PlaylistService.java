package com.example.anghamna.MusicService.Services;



import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.command.AddSongCommand;
import com.example.anghamna.MusicService.command.RemoveSongCommand;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service

public class PlaylistService {

    @Autowired
    private final PlaylistRepository playlistRepository;

    @Autowired private SongRepository songRepository;


    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Playlist createPlaylist(Playlist playlist) {
        Set<Song> inputSongs = playlist.getSongs();

        if (inputSongs == null) {
            playlist.setSongs(new HashSet<>());
        } else if (!inputSongs.isEmpty()) {
            List<UUID> songIds = inputSongs.stream()
                    .map(Song::getId)
                    .filter(Objects::nonNull)
                    .toList();

            List<Song> songList = songRepository.findAllById(songIds);
            Set<Song> managedSongs = new HashSet<>(songList);
            playlist.setSongs(managedSongs);
        }

        return playlistRepository.save(playlist);

    }

    public Set<Playlist> getAllPlaylists() {
        List<Playlist> playlists = playlistRepository.findAll();
        Set<Playlist> playlists_set = new HashSet<>(playlists);
        return playlists_set;

    }

    @Cacheable(value = "playlists",key = "#id")
    public Optional<Playlist> getPlaylistById(UUID id) {
        return playlistRepository.findById(id);
    }

    @Cacheable(value = "playlists",key = "#ownerId")
    public Set<Playlist> getPlaylistsByUserId(UUID ownerId) {
        return playlistRepository.findByOwnerId(ownerId);
    }


    // return all public playlists
    public Set<Playlist> getPublicPlaylists() {
       return playlistRepository.findByIsPrivate(false);
    }


    @Cacheable(value = "playlists",key = "#playlistId")
    public Set<Song> getPlaylistSongs(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        return playlist.getSongs();
    }


    @CachePut(value="playlists",key="#result.id")
    public Optional<Playlist> updatePlaylist(UUID playlistId, Playlist playlist) {

    return playlistRepository.findById(playlistId)
                .map(existingPlaylist -> {
                    existingPlaylist.setName(playlist.getName());
                    existingPlaylist.setPrivate(playlist.isPrivate());
                    existingPlaylist.setOwnerId(playlist.getOwnerId());
                    return playlistRepository.save(existingPlaylist);
                });
    }

    // DELETE
    @CacheEvict(value = "playlists", key = "#id")
    public void deletePlaylist(UUID id, UUID ownerId) {
        if (!playlistRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new NoSuchElementException("Playlist not found");
        }
        playlistRepository.deleteById(id);
    }

    public void togglePrivacy(UUID id, UUID ownerId) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found"));

        if (playlist.getOwnerId().equals(ownerId)) {
            playlist.setPrivate(!playlist.isPrivate());
            playlistRepository.save(playlist);
        } else {
            throw new SecurityException("You do not own this playlist.");
        }
    }

    public void addSong(AddSongCommand command) {
            command.execute();

    }

    public void removeSong(RemoveSongCommand command) {
        command.execute();

    }


    @Transactional
    @CacheEvict(value = "playlists", key = "#songId")
    public void deleteSongFromAllPlaylists(UUID songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + songId));

        List<Playlist> playlists = playlistRepository.findBySongsId(songId);

        playlists.forEach(playlist -> {
            boolean removed = playlist.getSongs().removeIf(s -> s.getId().equals(songId));

            if (removed) {
                song.getPlaylists().remove(playlist);
                playlistRepository.save(playlist);
            }
        });

        if (!playlists.isEmpty()) {
            songRepository.save(song);
        }
    }


    public void saveAllPlaylists(Set<Playlist> playlists) {
        playlistRepository.saveAll(playlists);
    }

}