package com.example.anghamna.UserService.Services;
import com.example.anghamna.UserService.DTOs.RegisterRequest;
import com.example.anghamna.UserService.DTOs.UserResponse;
import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Models.UserType;
import com.example.anghamna.UserService.Repositories.FollowRepository;
import com.example.anghamna.UserService.Repositories.UserRepository;
import jakarta.transaction.Transactional;
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
    private final FollowRepository followRepository;


    @Autowired
    public UserService(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
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
    public User updateUser(int id, RegisterRequest registerRequest) {
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (registerRequest.getUsername() != null) {
            existingUser.setUsername(registerRequest.getUsername());
        }

        if (registerRequest.getEmail() != null) {
            existingUser.setEmail(registerRequest.getEmail());
        }

        UserType userType;
        try {
            userType = UserType.valueOf(registerRequest.getUser_type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type");
        }

        if (registerRequest.getUser_type() != null) {
            existingUser.setUser_type(userType);
        }

        if (registerRequest.getBio() != null) {
            existingUser.setBio(registerRequest.getBio());
        }
        if (registerRequest.getPassword() != null && !registerRequest.getPassword().isBlank()) {
            String hashed = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt());
            existingUser.setPassword_hash(hashed);
        }

        return userRepository.save(existingUser);
    }

    @Cacheable(value = "user_cache",key = "#id")
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    public UserResponse getUserByIdE(int id) {
        User user = getUserById(id);
        return user != null ? new UserResponse(user) : null;
    }
//caching by3ml moshkla
    @Cacheable(value = "user_cache",key = "#result.id")
    public User getUserByUsername(String username) {
        User userName = userRepository.findByUsername(username);
        if (userName != null) {
            return userName;
        } else {
            throw new RuntimeException("User not found");
        }
    }
    public UserResponse getUserByUsernameE(String username) {
        User user = getUserByUsername(username);
        return user != null ? new UserResponse(user) : null;
    }

    @Transactional
    @CacheEvict(value = "user_cache",key = "#id")
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete follow records where the user is follower or followed
        followRepository.deleteByFollowerId(id);
        followRepository.deleteByFollowedId(id);

        // Now safely delete the user
        userRepository.delete(user);
    }


}
