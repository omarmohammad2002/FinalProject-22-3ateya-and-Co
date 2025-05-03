package com.spotify.music.service;

import com.spotify.music.model.Song;
import com.spotify.music.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSongById(UUID id) {
        return songRepository.findById(id);
    }

    public List<Song> getSongsByArtist(UUID artistId) {
        return songRepository.findByArtistId(artistId);
    }

    public List<Song> getSongsByGenre(String genre) {
        return songRepository.findByGenreIgnoreCase(genre);
    }

    public List<Song> searchSongsByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCase(title);
    }


    public Optional<Song> updateSong(UUID id, Song updatedSong) {
        return songRepository.findById(id).map(existingSong -> {
            existingSong.setTitle(updatedSong.getTitle());
            existingSong.setGenre(updatedSong.getGenre());
            existingSong.setDuration(updatedSong.getDuration());
            existingSong.setArtistId(updatedSong.getArtistId());
            return songRepository.save(existingSong);
        });
    }

    public boolean deleteSong(UUID id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // New: Top Streamed Songs
    public List<Song> getTopStreamedSongs(int limit) {
        return songRepository.findTopByOrderByStreamCountDesc(limit);
    }

    // New: Get Random Song
    public Optional<Song> getRandomSong() {
        List<Song> allSongs = songRepository.findAll();
        if (allSongs.isEmpty()) return Optional.empty();
        Random random = new Random();
        return Optional.of(allSongs.get(random.nextInt(allSongs.size())));
    }
}
