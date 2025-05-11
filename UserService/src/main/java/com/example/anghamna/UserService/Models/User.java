package com.example.anghamna.UserService.Models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 50, unique = true, nullable = false)
    private String username;
    @Column(length = 100, unique = true, nullable = false)
    private String email;
    @Column(length = 255, nullable = false)
    private String password_hash;
    @Column(length = 255)
    private String bio;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType user_type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        created_at = Date.from(now);
        updated_at = Date.from(now);
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = Date.from(Instant.now());
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
    public String getPassword_hash() {
        return password_hash;
    }
    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
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
    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

}
