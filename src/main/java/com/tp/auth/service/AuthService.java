package com.tp.auth.service;

import org.mindrot.jbcrypt.BCrypt;
import com.tp.auth.dao.UserDao;
import com.tp.auth.model.User;

public class AuthService {

    private final UserDao dao = new UserDao();

    // Inscription : ajoute la vérification du confirmPassword
    public String register(String username, String password, String confirmPassword) throws Exception {
        if (username == null || username.isBlank()) {
            return "Nom d'utilisateur obligatoire";
        }
        if (password == null || password.length() < 8) {
            return "Mot de passe : 8 caractères minimum";
        }
        // Règle métier : les deux saisies doivent être identiques
        if (!password.equals(confirmPassword)) {
            return "Les mots de passe ne correspondent pas";
        }
        if (dao.existsByUsername(username)) {
            return "Ce nom d'utilisateur existe déjà";
        }

        dao.save(username, password);
        return null;
    }

    // Connexion : inchangée
    public User login(String username, String password) throws Exception {
        User user = dao.findByUsername(username);
        if (user == null) {
            return null;
        }

        // 1. Vérifier si le compte est actuellement verrouillé
        if (user.getLockedUntil() != null && user.getLockedUntil().after(new java.util.Date())) {
            throw new Exception("Compte bloqué temporairement suite à 3 tentatives échouées.");
        }

        // 2. Vérifier le mot de passe
        boolean valid = false;
        try {
            valid = BCrypt.checkpw(password, user.getPasswordHash());
        } catch (IllegalArgumentException e) {
            valid = false;
        }

        if (valid) {
            // Connexion réussie : réinitialisation du compteur d'échecs
            dao.resetFailedAttempts(user.getId());
            return user;
        } else {
            // Mot de passe incorrect : enregistrer l'échec
            dao.recordFailedAttempt(user.getId(), user.getFailedAttempts());
            return null;
        }
    }
    
    // Permet à la vue de demander la liste complète des utilisateurs
    public java.util.List<User> getAllUsers() throws Exception {
        return dao.findAll();
    }
    
    public String changePassword(User user, String oldPassword, String newPassword, String confirmNewPassword) throws Exception {
        // 1. Vérifier que l'ancien mot de passe est correct
        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            return "L'ancien mot de passe est incorrect";
        }
        // 2. Vérifier la longueur du nouveau mot de passe
        if (newPassword == null || newPassword.length() < 8) {
            return "Le nouveau mot de passe doit faire au moins 8 caractères";
        }
        // 3. Vérifier que les deux nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmNewPassword)) {
            return "Les nouveaux mots de passe ne correspondent pas";
        }

        // 4. Mettre à jour en BDD
        dao.updatePassword(user.getId(), newPassword);
        return null;
    }
}