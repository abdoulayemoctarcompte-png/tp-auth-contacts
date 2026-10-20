package com.tp.auth.ui;

import java.awt.*;
import javax.swing.*;
import com.tp.auth.model.Contact;
import com.tp.auth.service.ContactService;

public class ContactDialog extends JDialog {
    private final JTextField nomField = new JTextField(15);
    private final JTextField prenomField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JTextField telephoneField = new JTextField(15);
    private final JLabel errorLabel = new JLabel("", SwingConstants.CENTER);

    private final ContactService contactService = new ContactService();
    private final int userId;
    private Contact contactToEdit;
    private boolean saved = false;

    public ContactDialog(JFrame parent, int userId, Contact contact) {
        super(parent, contact == null ? "Ajouter un contact" : "Modifier un contact", true);
        this.userId = userId;
        this.contactToEdit = contact;

        setSize(380, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Formulaire (Grille 4 lignes, 2 colonnes)
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        
        form.add(new JLabel("Nom : *"));
        form.add(nomField);
        form.add(new JLabel("Prénom : *"));
        form.add(prenomField);
        form.add(new JLabel("Email :"));
        form.add(emailField);
        form.add(new JLabel("Téléphone :"));
        form.add(telephoneField);

        // Si on est en mode modification, on pré-remplit les champs
        if (contactToEdit != null) {
            nomField.setText(contactToEdit.getNom());
            prenomField.setText(contactToEdit.getPrenom());
            emailField.setText(contactToEdit.getEmail() != null ? contactToEdit.getEmail() : "");
            telephoneField.setText(contactToEdit.getTelephone() != null ? contactToEdit.getTelephone() : "");
        }

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        // Zone de message d'erreur
        errorLabel.setForeground(Color.RED);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(errorLabel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(form, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Événements
        saveBtn.addActionListener(e -> saveContact());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void saveContact() {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String telephone = telephoneField.getText().trim();

            // Création ou mise à jour de l'objet Contact
            Contact c;
            if (contactToEdit == null) {
                c = new Contact(userId, nom, prenom, email, telephone);
            } else {
                c = contactToEdit;
                c.setNom(nom);
                c.setPrenom(prenom);
                c.setEmail(email);
                c.setTelephone(telephone);
            }

            // Appel au service pour validation et persistance
            String error = contactService.saveContact(c);
            if (error == null) {
                saved = true;
                dispose(); // Ferme le dialogue en cas de succès[cite: 48]
            } else {
                errorLabel.setText(error); // Affiche l'erreur de validation dans le formulaire[cite: 48]
            }
        } catch (Exception ex) {
            errorLabel.setText("Erreur : " + ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}