package com.airline.service;

import com.airline.model.Flight;

import java.util.List;

public interface FlightService {
    List<Flight> searchFlights(String source, String destination);
    List<Flight> getAllFlights();
    Flight getFlightById(int id);
}
