package com.tp.auth.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.tp.auth.db.Database;
import com.tp.auth.model.Contact;

public class ContactDao {

    // 1. Récupérer tous les contacts d'un utilisateur, triés par nom puis prénom
    public List<Contact> findByUser(int userId) throws Exception {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT id, user_id, nom, prenom, email, telephone FROM contacts WHERE user_id = ? ORDER BY nom ASC, prenom ASC";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new Contact(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    ));
                }
            }
        }
        return contacts;
    }

    // 2. Insérer un nouveau contact
    public void insert(Contact c) throws Exception {
        String sql = "INSERT INTO contacts (user_id, nom, prenom, email, telephone) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getNom());
            ps.setString(3, c.getPrenom());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getTelephone());
            ps.executeUpdate();
        }
    }

    // 3. Modifier un contact (sécurisé par id et user_id)
    public void update(Contact c) throws Exception {
        String sql = "UPDATE contacts SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelephone());
            ps.setInt(5, c.getId());
            ps.setInt(6, c.getUserId());
            ps.executeUpdate();
        }
    }

    // 4. Supprimer un contact (sécurisé par id et user_id)
    public void delete(int id, int userId) throws Exception {
        String sql = "DELETE FROM contacts WHERE id = ? AND user_id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // 5. Rechercher des contacts par nom, prénom ou email pour un utilisateur
    public List<Contact> search(int userId, String text) throws Exception {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT id, user_id, nom, prenom, email, telephone FROM contacts " +
                     "WHERE user_id = ? AND (nom LIKE ? OR prenom LIKE ? OR email LIKE ?) " +
                     "ORDER BY nom ASC, prenom ASC";
        
        String queryParam = "%" + text + "%";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, queryParam);
            ps.setString(3, queryParam);
            ps.setString(4, queryParam);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new Contact(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    ));
                }
            }
        }
        return contacts;
    }
}