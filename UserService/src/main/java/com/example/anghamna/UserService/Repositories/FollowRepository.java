package com.example.anghamna.UserService.Repositories;

import com.example.anghamna.UserService.Models.Follow;
import com.example.anghamna.UserService.Models.FollowId;
import org.hibernate.boot.archive.internal.JarProtocolArchiveDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByFollowerId(UUID followerId);
    List<Follow> findByFollowedId(UUID followedId);
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
    void deleteByFollowerId(UUID followerId);
    void deleteByFollowedId(UUID followedId);
}