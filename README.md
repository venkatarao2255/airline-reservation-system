# ✈️ Airline Reservation System

A comprehensive full-stack web application for booking and managing airline reservations with a modern React frontend and robust Java backend infrastructure.

---

## 🎯 Project Overview

The **Airline Reservation System** is a full-featured booking platform that enables users to search for flights, make reservations, and manage their bookings efficiently. Built with React for dynamic UI interactions and Java for reliable backend processing, the system demonstrates modern web development practices.

---

## ✨ Key Features

- **Flight Search & Filtering** - Search flights by source, destination, and date with real-time results
- **Booking Management** - Reserve flights with unique booking codes and track booking status
- **User Authentication** - Secure user registration and login with role-based access control
- **Seat Availability** - Real-time seat management and availability tracking
- **Booking History** - View and manage past and upcoming reservations
- **Responsive UI** - Mobile-friendly interface built with React
- **Admin Dashboard** - Manage flights, users, and bookings (admin role)

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Frontend** | React, JavaScript (ES6+), CSS |
| **Backend** | Java |
| **Database** | MySQL |
| **Architecture** | REST API / Layered Architecture |
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
