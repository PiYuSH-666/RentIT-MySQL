
CREATE DATABASE IF NOT EXISTS rentit_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rentit_db;

CREATE TABLE IF NOT EXISTS customers (
    username VARCHAR(80) NOT NULL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    credits DECIMAL(12,2) NOT NULL DEFAULT 5000.00
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vehicle_availability (
    vehicle_id VARCHAR(30) NOT NULL PRIMARY KEY,
    available_count INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_vehicle_available_count CHECK (available_count >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_username VARCHAR(80) NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    vehicle_id VARCHAR(30) NOT NULL,
    vehicle_model VARCHAR(120) NOT NULL,
    days INT NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    return_location VARCHAR(255) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bookings_customer (customer_username),
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_username)
        REFERENCES customers(username) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_booking_days CHECK (days > 0),
    CONSTRAINT chk_booking_total CHECK (total_price >= 0)
) ENGINE=InnoDB;

SHOW TABLES;

