package com.example.anghamna.UserService.Services;

import com.example.anghamna.UserService.Models.Session;
import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Repositories.SessionRepository;
import com.example.anghamna.UserService.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserService userService;


    public Session login(String username, String password) {
        User user = userService.getUserByUsername(username);
        if (!BCrypt.checkpw(password, user.getPassword_hash())) {
            throw new RuntimeException("Invalid username or password");
        }
        Session existingSession = sessionRepository.findFirstByUserAndExpiredAtAfter(user, Date.from(Instant.now()));
        if (existingSession != null) {
            throw new RuntimeException("You are already logged in. Please log out or wait for session to expire.");
        }

        Session session = new Session();
        session.setUser(user);
        session.setCreatedAt(Date.from(Instant.now()));
        session.setExpiredAt(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)));

        return sessionRepository.save(session);
    }

    public void logout(int sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty()) {
            throw new RuntimeException("Session not found");
        }
        sessionRepository.deleteById(sessionId);
    }

    public int validateSession(int sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getExpiredAt().before(Date.from(Instant.now()))) {
            throw new RuntimeException("Invalid or expired session.");
        }

        return sessionOptional.get().getUser().getId(); // return the user ID
    }
}
