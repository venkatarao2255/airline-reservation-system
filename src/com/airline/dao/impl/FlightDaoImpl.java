package com.airline.dao.impl;

import com.airline.dao.FlightDao;
import com.airline.model.Flight;
import com.airline.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightDaoImpl implements FlightDao {
    private static final String SEARCH = "SELECT id, flight_number, source, destination, departure_time, arrival_time, available_seats, price, airline FROM flights WHERE source LIKE ? AND destination LIKE ? AND available_seats > 0 ORDER BY departure_time";
    private static final String FIND_ALL = "SELECT id, flight_number, source, destination, departure_time, arrival_time, available_seats, price, airline FROM flights WHERE available_seats > 0 ORDER BY departure_time";
    private static final String FIND_BY_ID = "SELECT id, flight_number, source, destination, departure_time, arrival_time, available_seats, price, airline FROM flights WHERE id = ?";
    private static final String DECREMENT = "UPDATE flights SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0";

    @Override
    public List<Flight> search(String source, String destination) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH)) {
            ps.setString(1, "%" + source + "%");
            ps.setString(2, "%" + destination + "%");
            return mapFlights(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public List<Flight> findAll() {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL)) {
            return mapFlights(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public Flight findById(int id) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            List<Flight> list = mapFlights(ps.executeQuery());
            return list.isEmpty() ? null : list.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public void decrementSeats(int flightId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(DECREMENT)) {
            ps.setInt(1, flightId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    private List<Flight> mapFlights(ResultSet rs) throws SQLException {
        List<Flight> list = new ArrayList<>();
        while (rs.next()) {
            Flight f = new Flight();
            f.setId(rs.getInt("id"));
            f.setFlightNumber(rs.getString("flight_number"));
            f.setSource(rs.getString("source"));
            f.setDestination(rs.getString("destination"));
            Timestamp dep = rs.getTimestamp("departure_time");
            Timestamp arr = rs.getTimestamp("arrival_time");
            if (dep != null) f.setDepartureTime(dep.toLocalDateTime());
            if (arr != null) f.setArrivalTime(arr.toLocalDateTime());
            f.setAvailableSeats(rs.getInt("available_seats"));
            f.setPrice(rs.getDouble("price"));
            f.setAirline(rs.getString("airline"));
            list.add(f);
        }
        return list;
    }
}
