package com.example.anghamna.MusicService.Services;


//import com.example.anghamna.MusicService.Clients.UserClient;
import com.example.anghamna.MusicService.Models.Song;

import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Repositories.SongRepository;
import com.example.anghamna.MusicService.observers.Subject;
import com.example.anghamna.MusicService.rabbitmq.MusicProducer;
import com.example.anghamna.MusicService.rabbitmq.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.anghamna.MusicService.observers.Observer;


import java.util.*;

@Service
public class SongService implements Subject {

    @Autowired
    private final SongRepository songRepository;
    @Autowired
    private final PlaylistService playlistService;

    @Autowired
    private MusicProducer musicProducer;
    //observer
    private List<Observer> observers;
    //feign client
//    @Autowired
//    private UserClient userClient;

    public SongService(SongRepository songRepository, MusicProducer musicProducer, PlaylistService playlistService) {
        this.songRepository = songRepository;
        this.musicProducer = musicProducer;
        this.observers = new ArrayList<>();
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

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Cacheable(value = "song_cache",key = "#id")
    public Optional<Song> getSongById(UUID id) {
        return songRepository.findById(id);
    }

    @Cacheable(value = "song_cache",key = "'artist_' + #artistId")
    public List<Song> getSongsByArtist(UUID artistId) {
        return songRepository.findByArtistId(artistId);
    }

    @Cacheable(value = "song_cache",key = "'genre_' + #genre.toLowerCase()")
    public List<Song> getSongsByGenre(String genre) {
        return songRepository.findByGenreIgnoreCase(genre);
    }

//    //revise this if it should be cachable or cacheput
//    @CachePut(value = "song_cache",key = "'title_' + #title.toLowerCase()")
//    public List<Song> searchSongsByTitle(String title) {
//        return songRepository.findByTitleContainingIgnoreCase(title);
//    }
    @CachePut(value = "song_cache",key = "#id")
    public Optional<Song> updateSong(UUID id, Song updatedSong) {
        return songRepository.findById(id).map(existingSong -> {
            existingSong.setTitle(updatedSong.getTitle());
            existingSong.setGenre(updatedSong.getGenre());
            existingSong.setDuration(updatedSong.getDuration());
            existingSong.setArtistId(updatedSong.getArtistId());
            return songRepository.save(existingSong);
        });
    }

    @CacheEvict(value = "song_cache", key = "#id")
    public boolean deleteSong(UUID id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            playlistService.deleteSongFromAllPlaylists(id);

            //notify streaming service that song is deleted
            musicProducer.sendSongDeleted(id);
            //notify observers
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
    public boolean streamedSong(UUID id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song == null) return false ;
        song.setStreamCount(song.getStreamCount() + 1);

        songRepository.save(song);
        return true;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_DELETED_QUEUE)
    public boolean  deleteSongsByArtist(UUID artistId) {
        List<Song> songs = songRepository.findByArtistId(artistId);
        if (!songs.isEmpty()) {
            songRepository.deleteAll(songs);
            // Notify observers for each deleted song
            for (Song song : songs) {
                //notify streaming service that song is deleted
                musicProducer.sendSongDeleted(song.getId());
                notifyObservers(song.getId());
            }
            return true;

        }
        //TODO Add in observer + fix logic order to be before the return true
        return false;
    }


    //observer
    @Override
    public void registerObserver(Observer o){
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o){
        observers.remove(o);
    }

    @Override
    public void notifyObservers(UUID songId) {
        for (Observer observer : observers) {
            observer.onSongDeleted(songId);
        }
    }




}
