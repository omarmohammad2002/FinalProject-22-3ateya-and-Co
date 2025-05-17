package com.example.anghamna.MusicService.command;

import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import java.util.UUID;

public class RemoveSongCommand  implements  PlaylistCommand{
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UUID playlistId;
    private final UUID songId;

    public RemoveSongCommand(PlaylistRepository playlistRepository, SongRepository songRepository,UUID playlistId, UUID songId) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.playlistId = playlistId;
        this.songId = songId;
    }

    @Override
    public void execute() {
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        Song song = songRepository.findById(songId).orElse(null);
        if (playlist != null) {
            boolean isPresent = playlist.getSongs().remove(song);
            playlistRepository.save(playlist);
            if (isPresent) {
                song.getPlaylists().remove(playlist);
                songRepository.save(song);
            }
            System.out.println("Song removed: " + songId + " from playlist: " + playlistId);
        } else {
            System.out.println("Playlist not found: " + playlistId);
        }
    }
}




