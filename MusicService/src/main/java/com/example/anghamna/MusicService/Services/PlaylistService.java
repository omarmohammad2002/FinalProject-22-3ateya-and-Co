package com.example.anghamna.MusicService.Services;



import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Models.Playlist;
//import lombok.RequiredArgsConstructor;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.command.AddSongCommand;
import com.example.anghamna.MusicService.command.PlaylistCommand;
import com.example.anghamna.MusicService.command.RemoveSongCommand;
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
        List<Song> inputSongs = playlist.getSongs();
        if (inputSongs != null && !inputSongs.isEmpty()) {
            List<UUID> songIds = inputSongs.stream()
                    .map(Song::getId)
                    .filter(Objects::nonNull)
                    .toList();

            List<Song> managedSongs = songRepository.findAllById(songIds);
            playlist.setSongs(managedSongs); // Only set managed ones
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);

        // EAGER fetch is safer if you directly return JPA entities
        savedPlaylist.getSongs().size(); // force loading if LAZY

        return savedPlaylist;
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    @Cacheable(value = "playlists",key = "#id")
    public Optional<Playlist> getPlaylistById(UUID id) {
        //FIXME should check if its private and if the user is the owner
        return playlistRepository.findById(id);
    }

    @Cacheable(value = "playlists",key = "#ownerId")
    public List<Playlist> getPlaylistsByUserId(UUID ownerId) {
        // should check if its private and if the user is the owner
        return playlistRepository.findByOwnerId(ownerId);
    }


    // return all public playlists
    public List<Playlist> getPublicPlaylists() {
       return playlistRepository.findByIsPrivate(false);
    }

//    // return all private playlists (by yser only)
//     should only be by the user
//    public List<Playlist> getPrivatePlaylists() {
//        return playlistRepository.findByIsPrivate(true);
//    }


    public List<Song> getPlaylistSongs(UUID playlistId) {
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


    public void deleteSongFromAllPlaylists(UUID songId){
        playlistRepository.deleteSongFromAllPlaylists(songId);
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