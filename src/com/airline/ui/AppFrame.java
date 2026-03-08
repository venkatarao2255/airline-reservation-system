package com.airline.ui;

import com.airline.model.User;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    private final User user;

    public AppFrame(User user) {
        this.user = user;
        setTitle("Airline Reservation - " + user.getName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search Flights", new FlightSearchPanel(user, this));
        tabs.addTab("My Bookings", new MyBookingsPanel(user));
        tabs.addTab("Profile", buildProfilePanel());
        getContentPane().add(tabs);
    }

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        panel.add(new JLabel("Name: " + (user.getName() != null ? user.getName() : "")), gbc);
        gbc.gridy = 1;
        panel.add(new JLabel("Username: " + (user.getUsername() != null ? user.getUsername() : "")), gbc);
        gbc.gridy = 2;
        panel.add(new JLabel("Email: " + (user.getEmail() != null ? user.getEmail() : "")), gbc);
        gbc.gridy = 3;
        panel.add(new JLabel("Mobile: " + (user.getMobile() != null ? user.getMobile() : "")), gbc);
        gbc.gridy = 4;
        panel.add(new JLabel("Role: " + user.getRole()), gbc);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 12, 8, 12);
        panel.add(logoutBtn, gbc);
        return panel;
    }
}
