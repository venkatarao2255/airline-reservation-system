package com.airline.api;

import com.airline.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStore {
    private final Map<String, User> tokenToUser = new ConcurrentHashMap<>();

    public String create(User user) {
        String token = UUID.randomUUID().toString();
        tokenToUser.put(token, user);
        return token;
    }

    public User get(String token) {
        if (token == null || token.isBlank()) return null;
        return tokenToUser.get(token);
    }

    public void revoke(String token) {
        if (token == null || token.isBlank()) return;
        tokenToUser.remove(token);
    }
}

