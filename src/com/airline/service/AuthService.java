package com.airline.service;

import com.airline.model.User;

public interface AuthService {
    User login(String username, String password);
    User register(String username, String password, String name, String email, String mobile);
}
