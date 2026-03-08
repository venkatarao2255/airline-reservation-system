package com.airline.service.impl;

import com.airline.dao.UserDao;
import com.airline.dao.impl.UserDaoImpl;
import com.airline.model.User;
import com.airline.service.AuthService;
import com.airline.util.PasswordUtil;
import com.airline.util.ValidationUtil;

public class AuthServiceImpl implements AuthService {
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            return null;
        }
        return new User(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getMobile(), user.getRole());
    }

    @Override
    public User register(String username, String password, String name, String email, String mobile) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        if (!ValidationUtil.isValidMobile(mobile)) {
            throw new IllegalArgumentException("Invalid mobile number (10 digits, starting with 6-9)");
        }
        String trimmedUsername = username.trim();
        if (userDao.findByUsername(trimmedUsername) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDao.findByEmail(email.trim()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setUsername(trimmedUsername);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setMobile(ValidationUtil.normalizeMobile(mobile));
        user.setRole("CUSTOMER");
        return userDao.save(user);
    }
}
