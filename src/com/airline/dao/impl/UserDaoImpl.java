package com.airline.dao.impl;

import com.airline.dao.UserDao;
import com.airline.model.User;
import com.airline.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {
    private static final String FIND_BY_USERNAME = "SELECT id, username, password_hash, name, email, mobile, role FROM users WHERE username = ?";
    private static final String FIND_BY_EMAIL = "SELECT id, username, password_hash, name, email, mobile, role FROM users WHERE email = ?";
    private static final String INSERT_USER = "INSERT INTO users (username, password_hash, name, email, mobile, role) VALUES (?, ?, ?, ?, ?, ?)";

    @Override
    public User findByUsername(String username) {
        return findUser(FIND_BY_USERNAME, username);
    }

    @Override
    public User findByEmail(String email) {
        return findUser(FIND_BY_EMAIL, email);
    }

    private User findUser(String sql, String param) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setRole(rs.getString("role"));
        return user;
    }

    @Override
    public User save(User user) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail() != null ? user.getEmail() : "");
            ps.setString(5, user.getMobile() != null ? user.getMobile() : "");
            ps.setString(6, user.getRole() != null ? user.getRole() : "CUSTOMER");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
