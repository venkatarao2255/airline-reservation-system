package com.airline.service.impl;

import com.airline.dao.FlightDao;
import com.airline.dao.impl.FlightDaoImpl;
import com.airline.model.Flight;
import com.airline.service.FlightService;

import java.util.List;

public class FlightServiceImpl implements FlightService {
    private final FlightDao flightDao = new FlightDaoImpl();

    @Override
    public List<Flight> searchFlights(String source, String destination) {
        return flightDao.search(source != null ? source.trim() : "", destination != null ? destination.trim() : "");
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightDao.findAll();
    }

    @Override
    public Flight getFlightById(int id) {
        return flightDao.findById(id);
    }
}
