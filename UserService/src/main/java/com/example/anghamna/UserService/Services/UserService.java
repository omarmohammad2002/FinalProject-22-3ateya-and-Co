package com.example.anghamna.UserService.Services;
import com.example.anghamna.UserService.DTOs.RegisterRequest;
import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Models.UserType;
import com.example.anghamna.UserService.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword_hash(), BCrypt.gensalt());
        user.setPassword_hash(hashedPassword);
        return userRepository.save(user);
    }

    @CachePut(value = "user_cache", key = "#result.id")
    public User registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Check if userType is valid
        UserType userType;
        try {
            userType = UserType.valueOf(request.getUser_type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type");
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Create and save the new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword_hash(hashedPassword);
        user.setBio(request.getBio());
        user.setUser_type(userType);  // Set the user type based on the request
        user.setCreated_at(Date.from(Instant.now()));
        user.setUpdated_at(Date.from(Instant.now()));

        return userRepository.save(user);
    }

    @CachePut(value = "user_cache",key = "#result.id")
    public User updateUser(UUID id, User userUpdate) {
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (userUpdate.getUsername() != null) {
            existingUser.setUsername(userUpdate.getUsername());
        }

        if (userUpdate.getEmail() != null) {
            existingUser.setEmail(userUpdate.getEmail());
        }

        if (userUpdate.getUser_type() != null) {
            existingUser.setUser_type(userUpdate.getUser_type());
        }

        if (userUpdate.getBio() != null) {
            existingUser.setBio(userUpdate.getBio());
        }

        if (userUpdate.getPassword_hash() != null && !userUpdate.getPassword_hash().isBlank()) {
            String hashed = BCrypt.hashpw(userUpdate.getPassword_hash(), BCrypt.gensalt());
            existingUser.setPassword_hash(hashed);
        }

        return userRepository.save(existingUser);
    }

    @Cacheable(value = "user_cache",key = "#id")
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "user_cache",key = "#result.id")
    public User getUserByUsername(String username) {
        User userName = userRepository.findByUsername(username);
        if (userName != null) {
            return userName;
        } else {
            throw new RuntimeException("User not found");
        }
    }
    @CacheEvict(value = "user_cache",key = "#id")
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
