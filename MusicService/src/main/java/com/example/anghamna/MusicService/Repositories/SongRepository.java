// SongRepository.java
package com.example.anghamna.MusicService.Repositories;


import com.example.anghamna.MusicService.Models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {

    List<Song> findByArtistId(UUID artistId);

    List<Song> findByGenreIgnoreCase(String genre);

    List<Song> findByTitleContainingIgnoreCase(String title);

    @Query(value = "SELECT * FROM songs ORDER BY stream_count DESC LIMIT :limit", nativeQuery = true)
    List<Song> findTopByOrderByStreamCountDesc(@Param("limit") int limit);
}
