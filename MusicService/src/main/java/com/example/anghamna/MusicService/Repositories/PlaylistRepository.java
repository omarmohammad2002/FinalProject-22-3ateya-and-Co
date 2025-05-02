// PlaylistRepository.java
package com.spotify.music.repository;

import com.spotify.music.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwnerId(Long ownerId);
    List<Playlist> findByIsPrivateFalse();
    Optional<Playlist> findByIdAndOwnerId(Long id, Long ownerId);
}