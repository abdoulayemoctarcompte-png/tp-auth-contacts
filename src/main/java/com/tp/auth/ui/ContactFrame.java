package com.tp.auth.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.tp.auth.model.Contact;
import com.tp.auth.model.User;
import com.tp.auth.service.ContactService;

public class ContactFrame extends JFrame {
    private final User currentUser;
    private final ContactService contactService = new ContactService();
    
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Contact> contactList;
    private JTextField searchField;

    public ContactFrame(User user) {
        super("Carnet de contacts - " + user.getUsername());
        this.currentUser = user;

        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 1. Panneau du haut : Infos utilisateur + Recherche + Déconnexion
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("Connecté : " + user.getUsername() + " (" + user.getRole() + ")");
        JButton logoutBtn = new JButton("Se déconnecter");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Rechercher");
        searchPanel.add(new JLabel("Rechercher :"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        JPanel topLeftPanel = new JPanel(new BorderLayout(5, 5));
        topLeftPanel.add(welcomeLabel, BorderLayout.NORTH);
        topLeftPanel.add(searchPanel, BorderLayout.SOUTH);

        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // 2. Tableau central (JTable)
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cellules non modifiables par double-clic[cite: 47]
            }
        };
        
        table = new JTable(tableModel);
        // Masquer la colonne ID pour l'esthétique tout en gardant l'index accessible si besoin
        table.removeColumn(table.getColumnModel().getColumn(0));
        JScrollPane scrollPane = new JScrollPane(table);

        // 3. Panneau du bas : Boutons d'action (Ajouter, Modifier, Supprimer)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton deleteBtn = new JButton("Supprimer");

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);

        // Assemblage global
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- ÉVÉNEMENTS ---

        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Touche Entrée

        addBtn.addActionListener(e -> {
            ContactDialog dialog = new ContactDialog(this, currentUser.getId(), null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshTable();
            }
        });

        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un contact à modifier.", "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Récupère le contact correspondant dans la liste affichée
            Contact selectedContact = contactList.get(selectedRow);
            ContactDialog dialog = new ContactDialog(this, currentUser.getId(), selectedContact);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshTable();
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un contact à supprimer.", "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Contact selectedContact = contactList.get(selectedRow);
            
            // Boîte de dialogue de confirmation
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Supprimer " + selectedContact.getNom() + " " + selectedContact.getPrenom() + " ?", 
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    contactService.deleteContact(selectedContact.getId(), currentUser.getId());
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Chargement initial
        refreshTable();
    }

    public void refreshTable() {
        try {
            contactList = contactService.getContactsByUser(currentUser.getId());
            populateTable(contactList);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        try {
            String keyword = searchField.getText().trim();
            contactList = contactService.searchContacts(currentUser.getId(), keyword);
            populateTable(contactList);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de recherche : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Contact> contacts) {
        tableModel.setRowCount(0);
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getNom(),
                c.getPrenom(),
                c.getEmail() != null ? c.getEmail() : "",
                c.getTelephone() != null ? c.getTelephone() : ""
            });
        }
    }
}