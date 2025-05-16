package com.example.anghamna.MusicService.Services;


//import com.example.anghamna.MusicService.Clients.UserClient;
import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;

import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
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
import com.example.anghamna.MusicService.observers.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    //feign client
//    @Autowired
//    private UserClient userClient;

    public SongService(SongRepository songRepository, MusicProducer musicProducer, PlaylistService playlistService, SongObserver songObserver) {
        this.songRepository = songRepository;
        this.musicProducer = musicProducer;
        this.songObserver = songObserver;
        this.playlistService = playlistService;
        //this.userClient = userClient; //FIXME we need to fetch it from the request or cookie?
    }

    public Song createSong(Song song) {
//        User user = userClient.getUserById(song.getArtistId());
//
//        if(user == null) {
//            throw new RuntimeException("User not found");
//        }

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

//    //revise this if it should be cachable or cacheput
//    @CachePut(value = "song_cache",key = "'title_' + #title.toLowerCase()")
//    public List<Song> searchSongsByTitle(String title) {
//        return songRepository.findByTitleContainingIgnoreCase(title);
//    }
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

            // Detach song from all playlists (Hibernate-aware)
            Set<Playlist> playlists = song.getPlaylists();
            for (Playlist playlist : playlists) {
                playlistService.deleteSongFromAllPlaylists(song.getId());
            }
            // Save all updated playlists
            playlistService.saveAllPlaylists(playlists);

            // Now delete song safely
            songRepository.delete(song);

            // Notify
            notifyObservers(id);
            return true;
        }
        return false;
    }


//    // New: Top Streamed Songs
//    @Cacheable(value = "song_cache",key = "'top_' + #limit")
//    public List<Song> getTopStreamedSongs(int limit) {
//        return songRepository.findTopByOrderByStreamCountDesc(limit);
//    }

//    // New: Get Random Song
//    public Optional<Song> getRandomSong() {
//        List<Song> allSongs = songRepository.findAll();
//        if (allSongs.isEmpty()) return Optional.empty();
//        Random random = new Random();
//        return Optional.of(allSongs.get(random.nextInt(allSongs.size())));
//    }


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

    @RabbitListener(queues = RabbitMQConfig.USER_DELETED_QUEUE)
    public boolean  deleteSongsByArtist(UUID artistId) {

        Set<Song> songs = songRepository.findByArtistId(artistId);
//        UUID hardcodedArtistID = UUID.fromString("b35a6f2c-972c-4dd3-876c-45a3a5ce0d1f");
//        Set<Song> songs = songRepository.findByArtistId(hardcodedArtistID);
        if (!songs.isEmpty()) {

            //songRepository.deleteAll(songs);
            // Notify observers for each deleted song
            for (Song song : songs) {
                //notify streaming service that song is deleted
//                musicProducer.sendSongDeleted(song.getId());
//                notifyObservers(song.getId());
                this.deleteSong(song.getId());
            }
            return true;

        }

        return false;
    }


    //observer
//    @Override
//    public void registerObserver(Observer o){
//        observers.add(o);
//    }
//
//    @Override
//    public void removeObserver(Observer o){
//        observers.remove(o);
//    }

    @Override
    public void notifyObservers(UUID songId) {
        //for (Observer observer : observers) {
        songObserver.onSongDeleted(songId);
        //}
    }




}
