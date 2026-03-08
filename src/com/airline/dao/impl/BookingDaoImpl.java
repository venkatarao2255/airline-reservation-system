package com.airline.dao.impl;

import com.airline.dao.BookingDao;
import com.airline.model.Booking;
import com.airline.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDaoImpl implements BookingDao {
    private static final String INSERT = "INSERT INTO bookings (booking_code, user_id, flight_id, passenger_name, passenger_email, passenger_mobile, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_USER = "SELECT b.id, b.booking_code, b.user_id, b.flight_id, b.passenger_name, b.passenger_email, b.passenger_mobile, b.status, b.booking_time, f.flight_number, f.source, f.destination, f.departure_time, f.price FROM bookings b JOIN flights f ON b.flight_id = f.id WHERE b.user_id = ? ORDER BY b.booking_time DESC";

    @Override
    public Booking save(Booking booking) {
        String code = "PNR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setInt(2, booking.getUserId());
            ps.setInt(3, booking.getFlightId());
            ps.setString(4, booking.getPassengerName());
            ps.setString(5, booking.getPassengerEmail());
            ps.setString(6, booking.getPassengerMobile());
            ps.setString(7, "CONFIRMED");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    booking.setId(rs.getInt(1));
                }
            }
            booking.setBookingCode(code);
            return booking;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER)) {
            ps.setInt(1, userId);
            return mapBookings(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    private List<Booking> mapBookings(ResultSet rs) throws SQLException {
        List<Booking> list = new ArrayList<>();
        while (rs.next()) {
            Booking b = new Booking();
            b.setId(rs.getInt("id"));
            b.setBookingCode(rs.getString("booking_code"));
            b.setUserId(rs.getInt("user_id"));
            b.setFlightId(rs.getInt("flight_id"));
            b.setPassengerName(rs.getString("passenger_name"));
            b.setPassengerEmail(rs.getString("passenger_email"));
            b.setPassengerMobile(rs.getString("passenger_mobile"));
            b.setStatus(rs.getString("status"));
            Timestamp bt = rs.getTimestamp("booking_time");
            if (bt != null) b.setBookingTime(bt.toLocalDateTime());
            b.setFlightNumber(rs.getString("flight_number"));
            b.setSource(rs.getString("source"));
            b.setDestination(rs.getString("destination"));
            Timestamp dep = rs.getTimestamp("departure_time");
            if (dep != null) b.setDepartureTime(dep.toLocalDateTime());
            b.setPrice(rs.getDouble("price"));
            list.add(b);
        }
        return list;
    }
}
