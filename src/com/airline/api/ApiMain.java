package com.airline.api;

public class ApiMain {
    public static void main(String[] args) {
        int port = 8080;
        String envPort = System.getenv("API_PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        new ApiServer().start(port);
    }
}

