package com.airline.service;

import com.airline.model.Booking;
import com.airline.model.User;

import java.util.List;

public interface BookingService {
    Booking createBooking(User user, int flightId, String passengerName, String passengerEmail, String passengerMobile);
    List<Booking> getUserBookings(User user);
}
