# ✈️ Airline Reservation System

A comprehensive full-stack web application for booking and managing airline reservations with an intuitive user interface and robust backend infrastructure.

---

## 🎯 Project Overview

The **Airline Reservation System** is a modern booking platform that enables users to search for flights, make reservations, and manage their bookings efficiently. The system is built with a clean architecture following industry best practices, making it scalable and maintainable.

---

## ✨ Key Features

- **Flight Search & Filtering** - Search flights by source, destination, and date
- **Booking Management** - Reserve flights with unique booking codes and track booking status
- **User Authentication** - Secure user registration and login with role-based access
- **Seat Availability** - Real-time seat management and availability tracking
- **Booking History** - View and manage past and upcoming reservations
- **Admin Dashboard** - Manage flights, users, and bookings (admin role)

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Backend** | Java |
| **Frontend** | HTML, CSS, JavaScript |
| **Database** | MySQL / SQL |
| **Architecture** | MVC Pattern / Layered Architecture |
| **Build Tools** | Batch Scripts |

---

## 📊 Database Schema

### Users Table
Stores user account information with role-based access control
```sql
- id (Primary Key)
- username, password_hash, name, email, mobile
- role (CUSTOMER / ADMIN)
- created_at (Timestamp)
