package com.airline.ui;

import com.airline.model.Flight;
import com.airline.model.User;
import com.airline.service.BookingService;
import com.airline.service.FlightService;
import com.airline.service.impl.BookingServiceImpl;
import com.airline.service.impl.FlightServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightSearchPanel extends JPanel {
    private final User user;
    private final JFrame parent;
    private final FlightService flightService = new FlightServiceImpl();
    private final BookingService bookingService = new BookingServiceImpl();
    private JTextField sourceField;
    private JTextField destField;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private List<Flight> currentFlights = java.util.Collections.emptyList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public FlightSearchPanel(User user, JFrame parent) {
        this.user = user;
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("From:"));
        sourceField = new JTextField(12);
        searchPanel.add(sourceField);
        searchPanel.add(new JLabel("To:"));
        destField = new JTextField(12);
        searchPanel.add(destField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        searchPanel.add(searchBtn);
        JButton showAllBtn = new JButton("Show All");
        showAllBtn.addActionListener(e -> loadAllFlights());
        searchPanel.add(showAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Flight", "From", "To", "Departure", "Arrival", "Seats", "Price (₹)", "Book"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        flightTable.getColumn("ID").setMinWidth(0);
        flightTable.getColumn("ID").setMaxWidth(0);
        flightTable.getColumn("ID").setWidth(0);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(flightTable);
        add(scroll, BorderLayout.CENTER);

        loadAllFlights();
    }

    private void doSearch() {
        String source = sourceField.getText().trim();
        String dest = destField.getText().trim();
        List<Flight> flights = flightService.searchFlights(source, dest);
        populateTable(flights);
    }

    private void loadAllFlights() {
        populateTable(flightService.getAllFlights());
    }

    private void populateTable(List<Flight> flights) {
        currentFlights = flights;
        tableModel.setRowCount(0);
        for (Flight f : flights) {
            Object[] row = {
                    f.getId(),
                    f.getFlightNumber(),
                    f.getSource(),
                    f.getDestination(),
                    f.getDepartureTime() != null ? f.getDepartureTime().format(FMT) : "",
                    f.getArrivalTime() != null ? f.getArrivalTime().format(FMT) : "",
                    f.getAvailableSeats(),
                    String.format("%.0f", f.getPrice()),
                    "Book"
            };
            tableModel.addRow(row);
        }
        flightTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = flightTable.columnAtPoint(e.getPoint());
                int row = flightTable.rowAtPoint(e.getPoint());
                if (row >= 0 && col == 8 && row < currentFlights.size()) {
                    openBookingDialog(currentFlights.get(row));
                }
            }
        });
    }

    private void openBookingDialog(Flight flight) {
        JDialog dialog = new JDialog(parent, "Book Flight - " + flight.getFlightNumber(), true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(parent);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(new JLabel(flight.getSource() + " → " + flight.getDestination() + " | ₹" + flight.getPrice()), gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        p.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setText(user.getName() != null ? user.getName() : "");
        p.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        emailField.setText(user.getEmail() != null ? user.getEmail() : "");
        p.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        p.add(new JLabel("Mobile:"), gbc);
        gbc.gridx = 1;
        JTextField mobileField = new JTextField(20);
        mobileField.setText(user.getMobile() != null ? user.getMobile() : "");
        p.add(mobileField, gbc);

        JLabel msgLabel = new JLabel(" ");
        msgLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        p.add(msgLabel, gbc);
        gbc.gridwidth = 1;

        JButton confirmBtn = new JButton("Confirm Booking");
        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String mobile = mobileField.getText().trim();
            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
                msgLabel.setText("All fields required");
                return;
            }
            try {
                var booking = bookingService.createBooking(user, flight.getId(), name, email, mobile);
                JOptionPane.showMessageDialog(dialog, "Booking confirmed!\nPNR: " + booking.getBookingCode() + "\nFlight: " + flight.getFlightNumber(), "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAllFlights();
            } catch (IllegalArgumentException ex) {
                msgLabel.setText(ex.getMessage());
            }
        });
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        p.add(confirmBtn, gbc);
        dialog.add(p);
        dialog.setVisible(true);
    }
}
