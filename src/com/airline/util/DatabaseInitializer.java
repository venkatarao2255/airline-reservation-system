package com.airline.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL," +
                    "mobile VARCHAR(15) NOT NULL," +
                    "role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            addColumnIfNotExists(conn, "users", "email", "VARCHAR(100) DEFAULT ''");
            addColumnIfNotExists(conn, "users", "mobile", "VARCHAR(15) DEFAULT ''");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS flights (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "flight_number VARCHAR(20) NOT NULL," +
                    "source VARCHAR(50) NOT NULL," +
                    "destination VARCHAR(50) NOT NULL," +
                    "departure_time DATETIME NOT NULL," +
                    "arrival_time DATETIME NOT NULL," +
                    "available_seats INT NOT NULL DEFAULT 60," +
                    "price DECIMAL(10,2) NOT NULL," +
                    "airline VARCHAR(50) DEFAULT 'Airline')");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "booking_code VARCHAR(20) NOT NULL UNIQUE," +
                    "user_id INT NOT NULL," +
                    "flight_id INT NOT NULL," +
                    "passenger_name VARCHAR(100) NOT NULL," +
                    "passenger_email VARCHAR(100) NOT NULL," +
                    "passenger_mobile VARCHAR(15) NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'CONFIRMED'," +
                    "booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (flight_id) REFERENCES flights(id))");
            seedFlightsIfEmpty(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void addColumnIfNotExists(Connection conn, String table, String column, String def) {
        try {
            conn.createStatement().executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + def);
        } catch (SQLException ignored) {
        }
    }

    private static void seedFlightsIfEmpty(Connection conn) throws SQLException {
        try (var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM flights")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String[] inserts = {
                        "INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, available_seats, price) VALUES ('AI101', 'Mumbai', 'Delhi', '2025-03-15 08:00:00', '2025-03-15 10:30:00', 60, 4500)",
                        "INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, available_seats, price) VALUES ('AI102', 'Delhi', 'Mumbai', '2025-03-15 14:00:00', '2025-03-15 16:30:00', 60, 4500)",
                        "INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, available_seats, price) VALUES ('6E201', 'Bangalore', 'Chennai', '2025-03-16 09:00:00', '2025-03-16 10:00:00', 60, 2500)",
                        "INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, available_seats, price) VALUES ('6E202', 'Chennai', 'Bangalore', '2025-03-16 18:00:00', '2025-03-16 19:00:00', 60, 2500)",
                        "INSERT INTO flights (flight_number, source, destination, departure_time, arrival_time, available_seats, price) VALUES ('UK301', 'Hyderabad', 'Mumbai', '2025-03-17 07:30:00', '2025-03-17 09:30:00', 60, 5200)"
                };
                try (Statement st = conn.createStatement()) {
                    for (String sql : inserts) st.executeUpdate(sql);
                }
            }
        }
    }
}
