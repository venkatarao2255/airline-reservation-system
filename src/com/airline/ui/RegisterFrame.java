package com.airline.ui;

import com.airline.model.User;
import com.airline.service.AuthService;
import com.airline.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final AuthService authService = new AuthServiceImpl();
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField mobileField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel messageLabel;

    public RegisterFrame() {
        setTitle("Airline Reservation - Register");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        nameField = new JTextField(22);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        emailField = new JTextField(22);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Mobile:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        mobileField = new JTextField(22);
        mobileField.setToolTipText("10 digits, e.g. 9876543210");
        panel.add(mobileField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        usernameField = new JTextField(22);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        passwordField = new JPasswordField(22);
        passwordField.setToolTipText("Minimum 6 characters");
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        confirmField = new JPasswordField(22);
        panel.add(confirmField, gbc);

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        JButton registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(120, 32));
        registerBtn.addActionListener(e -> doRegister());
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> openLogin());
        gbc.gridy = 9;
        panel.add(backBtn, gbc);

        getContentPane().add(panel);
        getRootPane().setDefaultButton(registerBtn);
    }

    private void doRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String mobile = mobileField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        messageLabel.setText(" ");
        if (name.isEmpty()) {
            messageLabel.setText("Please enter your full name");
            return;
        }
        if (email.isEmpty()) {
            messageLabel.setText("Please enter your email");
            return;
        }
        if (mobile.isEmpty()) {
            messageLabel.setText("Please enter your mobile number");
            return;
        }
        if (username.isEmpty()) {
            messageLabel.setText("Please enter a username");
            return;
        }
        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match");
            return;
        }
        try {
            User user = authService.register(username, password, name, email, mobile);
            messageLabel.setForeground(new Color(0, 128, 0));
            messageLabel.setText("Registration successful! Redirecting to login...");
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException ignored) {
                }
                dispose();
                LoginFrame login = new LoginFrame();
                login.setPrefilledUsername(username);
                login.setVisible(true);
            });
        } catch (IllegalArgumentException ex) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void openLogin() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
