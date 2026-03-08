package com.airline.service.impl;

import com.airline.dao.BookingDao;
import com.airline.dao.FlightDao;
import com.airline.dao.impl.BookingDaoImpl;
import com.airline.dao.impl.FlightDaoImpl;
import com.airline.model.Booking;
import com.airline.model.Flight;
import com.airline.model.User;
import com.airline.service.BookingService;
import com.airline.util.ValidationUtil;

import java.util.List;

public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao = new BookingDaoImpl();
    private final FlightDao flightDao = new FlightDaoImpl();

    @Override
    public Booking createBooking(User user, int flightId, String passengerName, String passengerEmail, String passengerMobile) {
        if (passengerName == null || passengerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name is required");
        }
        if (passengerEmail == null || passengerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger email is required");
        }
        if (!ValidationUtil.isValidEmail(passengerEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (passengerMobile == null || passengerMobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger mobile is required");
        }
        if (!ValidationUtil.isValidMobile(passengerMobile)) {
            throw new IllegalArgumentException("Invalid mobile number");
        }
        Flight flight = flightDao.findById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found");
        }
        if (flight.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No seats available");
        }
        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setFlightId(flightId);
        booking.setPassengerName(passengerName.trim());
        booking.setPassengerEmail(passengerEmail.trim().toLowerCase());
        booking.setPassengerMobile(ValidationUtil.normalizeMobile(passengerMobile));
        flightDao.decrementSeats(flightId);
        Booking saved = bookingDao.save(booking);
        saved.setFlightNumber(flight.getFlightNumber());
        saved.setSource(flight.getSource());
        saved.setDestination(flight.getDestination());
        saved.setDepartureTime(flight.getDepartureTime());
        saved.setPrice(flight.getPrice());
        return saved;
    }

    @Override
    public List<Booking> getUserBookings(User user) {
        return bookingDao.findByUserId(user.getId());
    }
}
