//package com.example.anghamna.UserService.Repositories;
//
//import com.example.anghamna.UserService.Models.Follow;
//import org.hibernate.boot.archive.internal.JarProtocolArchiveDescriptor;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface FollowRepository extends JpaRepository<Follow, UUID> {
//    List<Follow> findByFollower_id(UUID followerId);
//    List<Follow> findByFollowed_id(UUID followedId);
//    boolean existsByFollower_idAndFollowed_id(UUID followerId, UUID followedId);
//    void deleteByFollower_idAndFollowed_id(UUID followerId, UUID followedId);
//
//}
