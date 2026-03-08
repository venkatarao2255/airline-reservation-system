package com.airline.ui;

import com.airline.model.Booking;
import com.airline.model.User;
import com.airline.service.BookingService;
import com.airline.service.impl.BookingServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyBookingsPanel extends JPanel {
    private final User user;
    private final BookingService bookingService = new BookingServiceImpl();
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public MyBookingsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"PNR", "Flight", "Route", "Passenger", "Email", "Mobile", "Departure", "Price (₹)", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        bookingTable = new JTable(tableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(bookingTable);
        add(scroll, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingService.getUserBookings(user);
        for (Booking b : bookings) {
            Object[] row = {
                    b.getBookingCode(),
                    b.getFlightNumber(),
                    b.getSource() + " → " + b.getDestination(),
                    b.getPassengerName(),
                    b.getPassengerEmail(),
                    b.getPassengerMobile(),
                    b.getDepartureTime() != null ? b.getDepartureTime().format(FMT) : "",
                    String.format("%.0f", b.getPrice()),
                    b.getStatus()
            };
            tableModel.addRow(row);
        }
        if (bookings.isEmpty()) {
            tableModel.addRow(new Object[]{"No bookings yet", "", "", "", "", "", "", "", ""});
        }
    }
}
