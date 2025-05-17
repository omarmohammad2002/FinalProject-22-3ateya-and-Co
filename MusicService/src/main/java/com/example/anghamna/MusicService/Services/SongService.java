package com.example.anghamna.MusicService.Services;


import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.observers.SongObserver;
import com.example.anghamna.MusicService.observers.Subject;
import com.example.anghamna.MusicService.rabbitmq.MusicProducer;
import com.example.anghamna.MusicService.rabbitmq.RabbitMQConfig;
import jakarta.persistence.PreRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SongService implements Subject {

    @Autowired
    private final SongRepository songRepository;
    @Autowired
    private final PlaylistService playlistService;
    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    @Autowired
    private MusicProducer musicProducer;
    //observer
    private SongObserver songObserver;

    public SongService(SongRepository songRepository, MusicProducer musicProducer, PlaylistService playlistService, SongObserver songObserver) {
        this.songRepository = songRepository;
        this.musicProducer = musicProducer;
        this.songObserver = songObserver;
        this.playlistService = playlistService;
    }

    public Song createSong(Song song) {
        return songRepository.save(song);
    }

    public Set<Song> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        Set<Song> songs_set = new HashSet<>(songs);
        return songs_set;

    }

    @Cacheable(value = "songs",key = "#id")
    public Optional<Song> getSongById(UUID id) {
        return songRepository.findById(id);
    }

    @Cacheable(value = "songs",key = "'artist_' + #artistId")
    public Set<Song> getSongsByArtist(UUID artistId) {
        return songRepository.findByArtistId(artistId);
    }

    @Cacheable(value = "songs",key = "'genre_' + #genre.toLowerCase()")
    public Set<Song> getSongsByGenre(String genre) {
        return songRepository.findByGenreIgnoreCase(genre);
    }

    @CachePut(value = "songs",key = "#id")
    public Optional<Song> updateSong(UUID id, Song updatedSong) {
        return songRepository.findById(id).map(existingSong -> {
            existingSong.setTitle(updatedSong.getTitle());
            existingSong.setGenre(updatedSong.getGenre());
            existingSong.setDuration(updatedSong.getDuration());
            existingSong.setArtistId(updatedSong.getArtistId());
            return songRepository.save(existingSong);
        });
    }

    @Transactional
    @PreRemove
    @CacheEvict(value = "songs", key = "#id")
    public boolean deleteSong(UUID id) {
        Optional<Song> songOptional = songRepository.findById(id);
        if (songOptional.isPresent()) {
            Song song = songOptional.get();

            Set<Playlist> playlists = song.getPlaylists();
            for (Playlist playlist : playlists) {
                playlistService.deleteSongFromAllPlaylists(song.getId());
            }
            playlistService.saveAllPlaylists(playlists);

            songRepository.delete(song);

            notifyObservers(id);
            return true;
        }
        return false;
    }

    public boolean likedSong(UUID id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song == null) return false;
        song.setLikeCount(song.getLikeCount() + 1);

        songRepository.save(song);
        return true;
    }

    //rabbitmq
    @RabbitListener(queues = RabbitMQConfig.SONG_STREAMED_QUEUE)
    public void streamedSong(String message) {
        UUID id = UUID.fromString(message);
        Song song = songRepository.findById(id).orElse(null);
        if (song == null) return  ;
        song.setStreamCount(song.getStreamCount() + 1);
        logger.info("🎧 Stream event received for songId: {}", id);
        songRepository.save(song);

    }

    @CacheEvict(value = "songs", key = "#artistId")
    @RabbitListener(queues = RabbitMQConfig.MUSIC_USER_DELETED_QUEUE)
    @Transactional
    public void  deleteSongsByArtist(String artistId) {
        UUID id = UUID.fromString(artistId);
        Set<Song> songs = songRepository.findByArtistId(id);
            if (!songs.isEmpty()) {
                for (Song song : songs) {
                    Set<Playlist> playlists = song.getPlaylists();
                    for (Playlist playlist : playlists) {
                        playlistService.deleteSongFromAllPlaylists(song.getId());
                        if(playlist.getOwnerId().equals(artistId)){
                            playlistService.deletePlaylist(playlist.getId(), id);
                        }
                    }

                    playlistService.saveAllPlaylists(playlists);

                    songRepository.delete(song);

                    notifyObservers(song.getId());
                }
            }
    }

    @Override
    public void notifyObservers(UUID songId) {
        songObserver.onSongDeleted(songId);
    }




}
