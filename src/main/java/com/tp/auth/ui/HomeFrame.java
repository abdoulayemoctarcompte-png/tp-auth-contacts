package com.tp.auth.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.tp.auth.model.User;
import com.tp.auth.service.AuthService;

public class HomeFrame extends JFrame {

    private final User currentUser;
    private final AuthService auth = new AuthService();

    public HomeFrame(User user) {
        super("Espace Utilisateur");
        this.currentUser = user;

        // 1. En-tête : Message personnalisé + badge de rôle
        JLabel welcomeLabel = new JLabel("Bienvenue, " + user.getUsername() + " (" + user.getRole() + ")", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // 2. Zone centrale : JTable uniquement si l'utilisateur est ADMIN
        JPanel centerPanel = new JPanel(new BorderLayout());
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            JTable userTable = createUsersTable();
            JScrollPane scrollPane = new JScrollPane(userTable);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des utilisateurs (ADMIN)"));
            centerPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            JLabel userMsg = new JLabel("Vous êtes connecté en tant qu'utilisateur standard.", SwingConstants.CENTER);
            centerPanel.add(userMsg, BorderLayout.CENTER);
        }

        // 3. Boutons d'action : Changement de mot de passe + Déconnexion
        JButton changePasswordBtn = new JButton("Changer le mot de passe");
        JButton logoutBtn = new JButton("Se déconnecter");

        changePasswordBtn.addActionListener(e -> openChangePasswordDialog());
        logoutBtn.addActionListener(e -> logout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(logoutBtn);

        // 4. Assemblage général
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.add(welcomeLabel, BorderLayout.NORTH);
        content.add(centerPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setSize(550, 380); // Redimensionné pour laisser de la place aux deux boutons
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Construit et remplit la JTable avec les données MySQL
    private JTable createUsersTable() {
        String[] columnNames = {"ID", "Nom d'utilisateur", "Rôle"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            List<User> users = auth.getAllUsers();
            for (User u : users) {
                Object[] row = { u.getId(), u.getUsername(), u.getRole() };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        return new JTable(model);
    }

    // Extension 04 : Boîte de dialogue pour modifier le mot de passe
    private void openChangePasswordDialog() {
        JPasswordField oldPf = new JPasswordField();
        JPasswordField newPf = new JPasswordField();
        JPasswordField confirmPf = new JPasswordField();

        Object[] message = {
            "Ancien mot de passe :", oldPf,
            "Nouveau mot de passe :", newPf,
            "Confirmer le nouveau mot de passe :", confirmPf
        };

        int option = JOptionPane.showConfirmDialog(
            this, message, "Changer le mot de passe",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                String error = auth.changePassword(
                    currentUser,
                    new String(oldPf.getPassword()),
                    new String(newPf.getPassword()),
                    new String(confirmPf.getPassword())
                );

                if (error == null) {
                    JOptionPane.showMessageDialog(this, "Mot de passe modifié avec succès !");
                } else {
                    JOptionPane.showMessageDialog(this, error, "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}