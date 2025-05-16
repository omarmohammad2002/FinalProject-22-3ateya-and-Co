package com.example.anghamna.MusicService.Services;



import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Models.Playlist;
//import lombok.RequiredArgsConstructor;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.command.AddSongCommand;
import com.example.anghamna.MusicService.command.PlaylistCommand;
import com.example.anghamna.MusicService.command.RemoveSongCommand;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PreRemove;
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
    // CREATE

    public Playlist createPlaylist(Playlist playlist) {
        Set<Song> inputSongs = playlist.getSongs();

        if (inputSongs == null) {
            playlist.setSongs(new HashSet<>()); // ensure it's never null
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
        //FIXME should check if its private and if the user is the owner
        return playlistRepository.findById(id);
    }

    @Cacheable(value = "playlists",key = "#ownerId")
    public Set<Playlist> getPlaylistsByUserId(UUID ownerId) {
        return playlistRepository.findByOwnerId(ownerId);

        // should check if its private and if the user is the owner
    }


    // return all public playlists
    public Set<Playlist> getPublicPlaylists() {
       return playlistRepository.findByIsPrivate(false);
    }

//    // return all private playlists (by yser only)
//     should only be by the user
//    public List<Playlist> getPrivatePlaylists() {
//        return playlistRepository.findByIsPrivate(true);
//    }

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

    //FIXME retrieve user id from cookie and verify with owner of playlist to delete
    @CacheEvict(value = "playlists", key = "#id")
    public void deletePlaylist(UUID id, UUID ownerId) {
        if (!playlistRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new NoSuchElementException("Playlist not found");
        }
        playlistRepository.deleteById(id);
    }

    //TODO fix this entire method bec it sends back 404
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

        // 2. Get only playlists containing this song
        List<Playlist> playlists = playlistRepository.findBySongsId(songId);

        // 3. Remove from both sides of the relationship
        playlists.forEach(playlist -> {
            // Remove song from playlist
            boolean removed = playlist.getSongs().removeIf(s -> s.getId().equals(songId));

            // Only update if the song was actually present
            if (removed) {
                // Remove playlist from song's collection
                song.getPlaylists().remove(playlist);
                playlistRepository.save(playlist);
            }
        });

        // 4. Explicitly save the song if it's been modified
        if (!playlists.isEmpty()) {
            songRepository.save(song);
        }
    }


    public void saveAllPlaylists(Set<Playlist> playlists) {
        playlistRepository.saveAll(playlists);
    }




    //UPDATE this is the dto thingy
//    // Mapper
//    private Playlist mapToResponse(Playlist playlist) {
//        return Playlist.builder()
//                .id(playlist.getId())
//                .name(playlist.getName())
//                .ownerId(playlist.getOwnerId())
//                .isPrivate(playlist.isPrivate())
//                .createdAt(playlist.getCreatedAt())
//                .updatedAt(playlist.getUpdatedAt())
//                .songCount(playlist.getSongs().size())
//                .build();
//    }
}