package com.example.anghamna.UserService.Controllers;


import com.example.anghamna.UserService.DTOs.LoginRequest;
import com.example.anghamna.UserService.Models.Session;
import com.example.anghamna.UserService.Services.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/userapi/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Session session = sessionService.login(loginRequest.getUsername(), loginRequest.getPassword());
        Cookie cookie = new Cookie("SESSION_ID", session.getId()+"");
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // available to all endpoints
        cookie.setMaxAge(2 * 60 * 60); // 2 hours
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged in successfully");
    }

    @DeleteMapping("/logout/{sessionId}")
    public String logout(@PathVariable UUID sessionId) {
        sessionService.logout(sessionId);
        return "Logged out successfully.";
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestHeader("X-Session-ID") UUID sessionId) {
        try {
            int userId = sessionService.validateSession(sessionId);
            return ResponseEntity.ok(userId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
