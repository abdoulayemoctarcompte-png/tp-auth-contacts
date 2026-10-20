package com.tp.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.tp.auth.db.Database;
import com.tp.auth.model.User;

public class UserDao {

    // 1. Vérifie si un nom d'utilisateur existe déjà
    public boolean existsByUsername(String username) throws Exception {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // 2. Enregistre un nouvel utilisateur (rôle USER par défaut)
    public void save(String username, String plainPassword) throws Exception {
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, 'USER')";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.executeUpdate();
        }
    }

    // 3. Recherche un utilisateur par pseudo (AVEC failed_attempts ET locked_until)
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT id, username, password_hash, role, failed_attempts, locked_until FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getInt("failed_attempts"),
                        rs.getTimestamp("locked_until")
                    );
                }
                return null;
            }
        }
    }

    // 4. Liste tous les utilisateurs (pour l'admin et JTable)
    public List<User> findAll() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, failed_attempts, locked_until FROM users";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    rs.getInt("failed_attempts"),
                    rs.getTimestamp("locked_until")
                ));
            }
        }
        return users;
    }

    // 5. Modification du mot de passe
    public void updatePassword(int userId, String newPlainPassword) throws Exception {
        String hash = BCrypt.hashpw(newPlainPassword, BCrypt.gensalt());
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // 6. Réinitialise les tentatives après une connexion réussie
    public void resetFailedAttempts(int userId) throws Exception {
        String sql = "UPDATE users SET failed_attempts = 0, locked_until = NULL WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // 7. Incrémente les échecs et verrouille (1 minute) au bout de 3 essais
    public void recordFailedAttempt(int userId, int currentAttempts) throws Exception {
        int newAttempts = currentAttempts + 1;
        String sql;
        if (newAttempts >= 3) {
            sql = "UPDATE users SET failed_attempts = ?, locked_until = DATE_ADD(NOW(), INTERVAL 1 MINUTE) WHERE id = ?";
        } else {
            sql = "UPDATE users SET failed_attempts = ? WHERE id = ?";
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAttempts);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}