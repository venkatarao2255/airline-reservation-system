package com.airline.dao;

import com.airline.model.Booking;

import java.util.List;

public interface BookingDao {
    Booking save(Booking booking);
    List<Booking> findByUserId(int userId);
}
