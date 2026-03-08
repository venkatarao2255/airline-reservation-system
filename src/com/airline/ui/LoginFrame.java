package com.airline.ui;

import com.airline.model.User;
import com.airline.service.AuthService;
import com.airline.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthServiceImpl();
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {
        setTitle("Airline Reservation - Login");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 32));
        loginBtn.addActionListener(e -> doLogin());
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginBtn, gbc);

        JButton registerBtn = new JButton("Create Account");
        registerBtn.addActionListener(e -> openRegister());
        gbc.gridy = 5;
        panel.add(registerBtn, gbc);

        getContentPane().add(panel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        messageLabel.setText(" ");
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }
        try {
            User user = authService.login(username, password);
            if (user != null) {
                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Login successful! Welcome, " + user.getName());
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new AppFrame(user).setVisible(true);
                });
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password");
            }
        } catch (Exception ex) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void openRegister() {
        dispose();
        new RegisterFrame().setVisible(true);
    }

    public void setPrefilledUsername(String username) {
        if (usernameField != null) {
            usernameField.setText(username);
        }
    }
}
