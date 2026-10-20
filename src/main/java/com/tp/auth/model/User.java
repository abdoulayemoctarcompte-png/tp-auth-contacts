package com.tp.auth.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private int failedAttempts;
    private Timestamp lockedUntil;

    public User(int id, String username, String passwordHash, String role, int failedAttempts, Timestamp lockedUntil) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public int getFailedAttempts() { return failedAttempts; }
    public Timestamp getLockedUntil() { return lockedUntil; }
}