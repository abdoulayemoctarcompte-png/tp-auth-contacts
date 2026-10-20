package com.tp.auth.model;

public class Contact {
    private int id;
    private int userId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Constructeur complet (pour la lecture depuis la base de données)
    public Contact(int id, int userId, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Constructeur sans id (pour la création avant l'insertion en base)
    public Contact(int userId, String nom, String prenom, String email, String telephone) {
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}