package com.airline.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBConnectionUtil {
    private static final String ENV_FILE = ".env";
    private static final Map<String, String> ENV_VALUES = new HashMap<>();
    private static String url;
    private static String username;
    private static String password;

    static {
        loadEnv();
        String host = getEnvValue("MYSQL_HOST");
        String port = getEnvValue("MYSQL_PORT");
        String database = getEnvValue("MYSQL_DATABASE");
        username = getEnvValue("MYSQL_USERNAME");
        password = getEnvValue("MYSQL_PASSWORD");
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    private static void loadEnv() {
        Path path = Paths.get(ENV_FILE);
        if (!Files.exists(path)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int index = trimmed.indexOf('=');
                if (index <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, index).trim();
                String value = trimmed.substring(index + 1).trim();
                if (!key.isEmpty()) {
                    ENV_VALUES.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read .env file", e);
        }
    }

    private static String getEnvValue(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        value = ENV_VALUES.get(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing environment value for " + key);
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Connected to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

