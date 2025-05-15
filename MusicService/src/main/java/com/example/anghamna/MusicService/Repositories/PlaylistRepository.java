package com.example.anghamna.MusicService.Repositories;

import com.example.anghamna.MusicService.Models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    @Query("SELECT p FROM Playlist p WHERE p.ownerId = :ownerId")
    List<Playlist> findByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT p FROM Playlist p WHERE p.id = :id AND p.ownerId = :ownerId")
    Optional<Playlist> findByIdAndOwnerId(@Param("id") UUID id, @Param("ownerId") UUID ownerId);

    @Query("SELECT p FROM Playlist p WHERE p.isPrivate = :isPrivate")
    List<Playlist> findByIsPrivate(@Param("isPrivate") boolean isPrivate);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Playlist p WHERE p.id = :id AND p.ownerId = :ownerId")
    boolean existsByIdAndOwnerId(@Param("id") UUID id, @Param("ownerId") UUID ownerId);
}
