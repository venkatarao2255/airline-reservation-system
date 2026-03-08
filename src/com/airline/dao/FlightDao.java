package com.airline.dao;

import com.airline.model.Flight;

import java.util.List;

public interface FlightDao {
    List<Flight> search(String source, String destination);
    List<Flight> findAll();
    Flight findById(int id);
    void decrementSeats(int flightId);
}
