package com.airline.dao;

import com.airline.model.User;

public interface UserDao {
    User findByUsername(String username);
    User findByEmail(String email);
    User save(User user);
}
