package com.example.anghamna.UserService.Repositories;

import com.example.anghamna.UserService.Models.Session;
import com.example.anghamna.UserService.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Session findFirstByUserAndExpiredAtAfter(User user, Date now);

}
