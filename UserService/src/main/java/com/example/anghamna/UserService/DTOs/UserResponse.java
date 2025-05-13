package com.example.anghamna.UserService.DTOs;

import com.example.anghamna.UserService.Models.User;
import com.example.anghamna.UserService.Models.UserType;

import java.util.Date;

public class UserResponse {
    private int id;
    private String username;
    private String email;
    private String bio;
    private UserType user_type;
    private Date created_at;
    private Date updated_at;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.user_type = user.getUser_type();
        this.created_at = user.getCreated_at();
        this.updated_at = user.getUpdated_at();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public UserType getUser_type() {
        return user_type;
    }
    public void setUser_type(UserType user_type) {
        this.user_type = user_type;
    }
    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public Date getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}

