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

public class SessionService {

    private static SessionService instance;

    private final SessionRepository sessionRepository;
    private final UserService userService;

    // 🔒 Private constructor to prevent external instantiation
    private SessionService(SessionRepository sessionRepository, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    // 🚪 Public method to access the single instance
    public static SessionService getInstance(SessionRepository sessionRepository, UserService userService) {
        if (instance == null) {
            synchronized (SessionService.class) {
                if (instance == null) {
                    instance = new SessionService(sessionRepository, userService);
                    System.out.println("SessionService instance created");
                }
            }
        }
        return instance;
    }

    // ✅ Original methods (login, logout, validateSession) remain the same
    public Session login(String username, String password) {
        User user = userService.getUserByUsername(username);
        if (!BCrypt.checkpw(password, user.getPassword_hash())) {
            throw new RuntimeException("Invalid username or password");
        }

        Session existingSession = sessionRepository.findFirstByUserAndExpiredAtAfter(user, Date.from(Instant.now()));
        if (existingSession != null) {
            throw new RuntimeException("You are already logged in.");
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

    public UUID validateSession(int sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getExpiredAt().before(Date.from(Instant.now()))) {
            throw new RuntimeException("Invalid or expired session.");
        }

        return sessionOptional.get().getUser().getId();
    }
}
