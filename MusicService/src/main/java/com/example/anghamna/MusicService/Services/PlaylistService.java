package com.example.anghamna.MusicService.Services;



import com.example.anghamna.MusicService.Models.Song;
import com.example.anghamna.MusicService.Repositories.PlaylistRepository;
import com.example.anghamna.MusicService.Models.Playlist;
//import lombok.RequiredArgsConstructor;
import com.example.anghamna.MusicService.command.AddSongCommand;
import com.example.anghamna.MusicService.command.PlaylistCommand;
import com.example.anghamna.MusicService.command.RemoveSongCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service

public class PlaylistService {

    @Autowired
    private final PlaylistRepository playlistRepository;


    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }
    // CREATE

    public Playlist createPlaylist(Playlist playlist) {
//        if (playlistRepository.existsByNameAndOwnerId(playlist.getName(), playlist.getOwnerId())) {
//            throw new DuplicateResourceException("Playlist name already exists");
//        }
        return playlistRepository.save(playlist);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    @Cacheable(value = "playlist_cache",key = "#id")
    public Optional<Playlist> getPlaylistById(UUID id) {
        //FIXME should check if its private and if the user is the owner
        return playlistRepository.findById(id);
    }

    @Cacheable(value = "playlist_cache",key = "#ownerId")
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


    @CachePut(value="playlist_cache",key="#id")
    public Optional<Playlist> updatePlaylist(UUID playlistId, Playlist playlist) {

    return playlistRepository.findById(playlistId)
                .map(existingPlaylist -> {
                    existingPlaylist.setName(playlist.getName());
                    existingPlaylist.setPrivate(playlist.isPrivate());
                    existingPlaylist.setSongs(playlist.getSongs());
                    return playlistRepository.save(existingPlaylist);
                });
    }

    // DELETE

    //FIXME retrieve user id from cookie and verify with owner of playlist to delete
    @CacheEvict(value = "playlist_cache", key = "#id")
    public void deletePlaylist(UUID id, UUID ownerId) {
        if (!playlistRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new NoSuchElementException("Playlist not found");
        }
        playlistRepository.deleteById(id);
    }


  public void togglePrivacy(UUID id, UUID ownerId) {
      Playlist playlist = playlistRepository.findByIdAndOwnerId(id, ownerId)
              .orElseThrow(() -> new NoSuchElementException("Playlist not found"));

      playlist.setPrivate(!playlist.isPrivate());
      playlistRepository.save(playlist);
  }

    public void addSong(AddSongCommand command) {
            command.execute();

    }

    public void removeSong(RemoveSongCommand command) {
        command.execute();

    }


    public void deleteSongFromAllPlaylists(UUID songId){
        List<Playlist> playlists = playlistRepository.findAll();
        for (Playlist playlist : playlists) {
            if (playlist.getSongs().removeIf(song -> song.getId().equals(songId))) {
                playlistRepository.save(playlist);
            }
        }
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