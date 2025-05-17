// SongRepository.java
package com.example.anghamna.MusicService.Repositories;


import com.example.anghamna.MusicService.Models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Set;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {

    @Query("SELECT s FROM Song s LEFT JOIN FETCH s.playlists WHERE s.artistId = :artistId")
    Set<Song> findByArtistId(@Param("artistId") UUID artistId);

    Set<Song> findByGenreIgnoreCase(String genre);

    Set<Song> findByTitleContainingIgnoreCase(String title);

}
