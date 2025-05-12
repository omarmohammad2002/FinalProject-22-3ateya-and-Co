// PlaylistRepository.java
package com.example.anghamna.MusicService.Repositories;


import com.example.anghamna.MusicService.Models.Playlist;
import com.example.anghamna.MusicService.Models.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {



    List<Playlist> findByOwnerId(UUID ownerId);
    Optional<Playlist> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Playlist> findByIsPrivate(boolean b);

    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);




}