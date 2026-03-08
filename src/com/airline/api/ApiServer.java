package com.airline.api;

import com.airline.model.Booking;
import com.airline.model.Flight;
import com.airline.model.User;
import com.airline.service.AuthService;
import com.airline.service.BookingService;
import com.airline.service.FlightService;
import com.airline.service.impl.AuthServiceImpl;
import com.airline.service.impl.BookingServiceImpl;
import com.airline.service.impl.FlightServiceImpl;
import com.airline.util.DatabaseInitializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiServer {
    private final SessionStore sessions = new SessionStore();
    private final AuthService authService = new AuthServiceImpl();
    private final FlightService flightService = new FlightServiceImpl();
    private final BookingService bookingService = new BookingServiceImpl();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private HttpServer server;

    public void start(int port) {
        try {
            DatabaseInitializer.initialize();
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/health", this::handleHealth);
            server.createContext("/api/auth/register", this::handleRegister);
            server.createContext("/api/auth/login", this::handleLogin);
            server.createContext("/api/flights", this::handleFlights);
            server.createContext("/api/bookings", this::handleBookings);
            server.setExecutor(null);
            server.start();
            System.out.println("API server running on http://localhost:" + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleHealth(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtil.sendEmpty(ex, 405);
            return;
        }
        HttpUtil.sendJson(ex, 200, JsonUtil.obj(Map.of("status", "ok")));
    }

    private void handleRegister(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtil.sendEmpty(ex, 405);
            return;
        }
        try {
            Map<String, String> body = JsonUtil.parseObject(HttpUtil.readBody(ex));
            User user = authService.register(
                    body.getOrDefault("username", ""),
                    body.getOrDefault("password", ""),
                    body.getOrDefault("name", ""),
                    body.getOrDefault("email", ""),
                    body.getOrDefault("mobile", "")
            );
            HttpUtil.sendJson(ex, 201, JsonUtil.obj(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "mobile", user.getMobile(),
                    "role", user.getRole()
            )));
        } catch (IllegalArgumentException e) {
            HttpUtil.sendJson(ex, 400, JsonUtil.obj(Map.of("error", e.getMessage())));
        } catch (Exception e) {
            HttpUtil.sendJson(ex, 500, JsonUtil.obj(Map.of("error", "Server error")));
        }
    }

    private void handleLogin(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtil.sendEmpty(ex, 405);
            return;
        }
        try {
            Map<String, String> body = JsonUtil.parseObject(HttpUtil.readBody(ex));
            User user = authService.login(body.getOrDefault("username", ""), body.getOrDefault("password", ""));
            if (user == null) {
                HttpUtil.sendJson(ex, 401, JsonUtil.obj(Map.of("error", "Invalid username or password")));
                return;
            }
            String token = sessions.create(user);
            Map<String, Object> userJson = new LinkedHashMap<>();
            userJson.put("id", user.getId());
            userJson.put("username", user.getUsername());
            userJson.put("name", user.getName());
            userJson.put("email", user.getEmail());
            userJson.put("mobile", user.getMobile());
            userJson.put("role", user.getRole());
            HttpUtil.sendJson(ex, 200, JsonUtil.obj(Map.of("token", token, "user", userJson)));
        } catch (Exception e) {
            HttpUtil.sendJson(ex, 500, JsonUtil.obj(Map.of("error", "Server error")));
        }
    }

    private void handleFlights(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtil.sendEmpty(ex, 405);
            return;
        }
        Map<String, String> q = HttpUtil.parseQuery(ex.getRequestURI().getRawQuery());
        String source = q.getOrDefault("source", "");
        String destination = q.getOrDefault("destination", "");
        List<Flight> flights;
        if ((source != null && !source.isBlank()) || (destination != null && !destination.isBlank())) {
            flights = flightService.searchFlights(source, destination);
        } else {
            flights = flightService.getAllFlights();
        }
        List<String> items = new ArrayList<>();
        for (Flight f : flights) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", f.getId());
            m.put("flightNumber", f.getFlightNumber());
            m.put("source", f.getSource());
            m.put("destination", f.getDestination());
            m.put("departureTime", f.getDepartureTime() != null ? f.getDepartureTime().format(fmt) : "");
            m.put("arrivalTime", f.getArrivalTime() != null ? f.getArrivalTime().format(fmt) : "");
            m.put("availableSeats", f.getAvailableSeats());
            m.put("price", f.getPrice());
            m.put("airline", f.getAirline());
            items.add(JsonUtil.obj(m));
        }
        HttpUtil.sendJson(ex, 200, "{\"items\":" + JsonUtil.arr(items) + "}");
    }

    private void handleBookings(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        User user = requireAuth(ex);
        if (user == null) return;
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            List<Booking> bookings = bookingService.getUserBookings(user);
            List<String> items = new ArrayList<>();
            for (Booking b : bookings) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", b.getId());
                m.put("bookingCode", b.getBookingCode());
                m.put("flightId", b.getFlightId());
                m.put("flightNumber", b.getFlightNumber());
                m.put("source", b.getSource());
                m.put("destination", b.getDestination());
                m.put("departureTime", b.getDepartureTime() != null ? b.getDepartureTime().format(fmt) : "");
                m.put("price", b.getPrice());
                m.put("status", b.getStatus());
                m.put("passengerName", b.getPassengerName());
                m.put("passengerEmail", b.getPassengerEmail());
                m.put("passengerMobile", b.getPassengerMobile());
                items.add(JsonUtil.obj(m));
            }
            HttpUtil.sendJson(ex, 200, "{\"items\":" + JsonUtil.arr(items) + "}");
            return;
        }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            try {
                Map<String, String> body = JsonUtil.parseObject(HttpUtil.readBody(ex));
                int flightId = Integer.parseInt(body.getOrDefault("flightId", "0"));
                Booking booking = bookingService.createBooking(
                        user,
                        flightId,
                        body.getOrDefault("passengerName", ""),
                        body.getOrDefault("passengerEmail", ""),
                        body.getOrDefault("passengerMobile", "")
                );
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", booking.getId());
                m.put("bookingCode", booking.getBookingCode());
                m.put("flightId", booking.getFlightId());
                m.put("flightNumber", booking.getFlightNumber());
                m.put("source", booking.getSource());
                m.put("destination", booking.getDestination());
                m.put("departureTime", booking.getDepartureTime() != null ? booking.getDepartureTime().format(fmt) : "");
                m.put("price", booking.getPrice());
                m.put("status", "CONFIRMED");
                HttpUtil.sendJson(ex, 201, JsonUtil.obj(m));
            } catch (NumberFormatException e) {
                HttpUtil.sendJson(ex, 400, JsonUtil.obj(Map.of("error", "Invalid flightId")));
            } catch (IllegalArgumentException e) {
                HttpUtil.sendJson(ex, 400, JsonUtil.obj(Map.of("error", e.getMessage())));
            } catch (Exception e) {
                HttpUtil.sendJson(ex, 500, JsonUtil.obj(Map.of("error", "Server error")));
            }
            return;
        }
        HttpUtil.sendEmpty(ex, 405);
    }

    private User requireAuth(HttpExchange ex) throws IOException {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        String token = null;
        if (auth != null) {
            String a = auth.trim();
            if (a.regionMatches(true, 0, "Bearer ", 0, 7)) token = a.substring(7).trim();
        }
        User u = sessions.get(token);
        if (u == null) {
            HttpUtil.sendJson(ex, 401, JsonUtil.obj(Map.of("error", "Unauthorized")));
            return null;
        }
        return u;
    }

    private boolean isOptions(HttpExchange ex) throws IOException {
        if (!"OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) return false;
        HttpUtil.sendEmpty(ex, 204);
        return true;
    }
}

