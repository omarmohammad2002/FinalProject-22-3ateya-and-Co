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
        try {
            Session session = sessionService.login(loginRequest.getUsername(), loginRequest.getPassword());

            // Create SESSION_ID cookie
            Cookie sessionCookie = new Cookie("SESSION_ID", session.getId() + "");
            sessionCookie.setHttpOnly(true); // can't be accessed via JS
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(2 * 60 * 60); // 2 hours
            response.addCookie(sessionCookie);

            // Create USER_ID cookie
            Cookie userIdCookie = new Cookie("USER_ID", session.getUser().getId().toString());
            userIdCookie.setHttpOnly(false); // optional: allow JS access if needed
            userIdCookie.setPath("/");
            userIdCookie.setMaxAge(2 * 60 * 60);
            response.addCookie(userIdCookie);

            return ResponseEntity.ok("Logged in successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @DeleteMapping("/logout/{sessionId}")
    public ResponseEntity<String> logout(@PathVariable int sessionId, HttpServletResponse response) {
        sessionService.logout(sessionId);

        // Delete SESSION_ID cookie
        Cookie sessionCookie = new Cookie("SESSION_ID", null);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0); // Delete immediately
        response.addCookie(sessionCookie);

        // Delete USER_ID cookie
        Cookie userIdCookie = new Cookie("USER_ID", null);
        userIdCookie.setPath("/");
        userIdCookie.setMaxAge(0); // Delete immediately
        response.addCookie(userIdCookie);

        return ResponseEntity.ok("Logged out successfully.");
    }


    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestParam("sessionId") int sessionId) {
        try {
            Boolean valid = sessionService.validateSession(sessionId);
            return ResponseEntity.ok(valid); // return boolean for Feign client
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

}
