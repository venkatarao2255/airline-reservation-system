package com.airline;

import com.airline.ui.LoginFrame;
import com.airline.util.DatabaseInitializer;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
