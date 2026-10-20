package com.tp.auth.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.tp.auth.model.User;
import com.tp.auth.service.AuthService;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JPasswordField confirmPasswordField = new JPasswordField(15);
    private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton loginBtn = new JButton("Se connecter");
    private final JButton registerBtn = new JButton("S'inscrire");
    private final AuthService auth = new AuthService();

    public LoginFrame() {
        super("Authentification");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Nom d'utilisateur :"));
        form.add(usernameField);
        form.add(new JLabel("Mot de passe :"));
        form.add(passwordField);
        form.add(new JLabel("Confirmer le mot de passe :"));
        form.add(confirmPasswordField);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
        getRootPane().setDefaultButton(loginBtn);

        JPanel buttons = new JPanel();
        buttons.add(loginBtn);
        buttons.add(registerBtn);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.add(form, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
        content.add(messageLabel, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        setLoading(true);
        showMessage("Connexion en cours...", Color.BLUE);

        // Traitement asynchrone pour ne pas geler l'interface
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return auth.login(username, password);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    User user = get();
                    if (user != null) {
                        dispose();
                        // Ouvre la fenêtre du carnet de contacts (TP 2)
                        new ContactFrame(user).setVisible(true);
                    } else {
                        showMessage("Identifiants incorrects", Color.RED);
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    showMessage("Erreur : " + cause.getMessage(), Color.RED);
                }
            }
        }.execute();
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        setLoading(true);
        showMessage("Inscription en cours...", Color.BLUE);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return auth.register(username, password, confirmPassword);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    String error = get();
                    if (error == null) {
                        showMessage("Compte créé, vous pouvez vous connecter", new Color(0, 130, 0));
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                    } else {
                        showMessage(error, Color.RED);
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    showMessage("Erreur : " + cause.getMessage(), Color.RED);
                }
            }
        }.execute();
    }

    private void setLoading(boolean loading) {
        loginBtn.setEnabled(!loading);
        registerBtn.setEnabled(!loading);
        usernameField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        confirmPasswordField.setEnabled(!loading);
    }

    private void showMessage(String text, Color color) {
        messageLabel.setForeground(color);
        messageLabel.setText(text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}