package com.example.anghamna.UserService.Services;

import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Repositories.SessionRepository;
import com.example.anghamna.UserService.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User user) {
        User oldUser = userRepository.findById(id).orElse(null);
        oldUser.setUsername(user.getUsername());
        oldUser.setEmail(user.getEmail());
        oldUser.setUser_type(user.getUser_type());
        oldUser.setBio(user.getBio());
        oldUser.setPassword_hash(user.getPassword_hash());
        oldUser.setUpdated_at(oldUser.getUpdated_at());
        oldUser.setCreated_at(oldUser.getCreated_at());
        return userRepository.save(oldUser);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
