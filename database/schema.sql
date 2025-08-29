-- =====================================================
-- Database Schema for Airport Management System
-- Author: Steive James
-- Description: This file contains the database schema 
--              for managing planes, pilots, hangars, 
--              and related details.
-- Created On: 2025-08-11
-- =====================================================

-- =========================
-- Table: address_details
-- =========================
CREATE TABLE address_details (
    address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    house_no VARCHAR(50) NOT NULL,
    street VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    phoneno VARCHAR(15) NOT NULL
);

-- =========================
-- Table: user_details
-- =========================
CREATE TABLE user_details (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin', 'manager') NOT NULL,
    date_of_birth DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    address_id BIGINT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address_details(address_id)
);

-- =========================
-- Table: plane_owner_details
-- =========================
CREATE TABLE plane_owner_details (
    owner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address_id BIGINT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address_details(address_id) ON DELETE CASCADE
);

-- =========================
-- Table: plane_details
-- =========================
CREATE TABLE plane_details (
    plane_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plane_number VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    owner_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES plane_owner_details(owner_id),
    FOREIGN KEY (user_id) REFERENCES user_details(user_id)
);

-- =========================
-- Table: pilot_details
-- =========================
CREATE TABLE pilot_details (
    pilot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    license_no VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_details(user_id),
    FOREIGN KEY (address_id) REFERENCES address_details(address_id)
);

-- =========================
-- Table: hangar_details
-- =========================
CREATE TABLE hangar_details (
    hangar_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hangar_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    hangar_location VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_details(user_id)
);

-- =========================
-- Table: plane_allocation
-- =========================
CREATE TABLE plane_allocation (
    plane_id BIGINT NOT NULL,
    pilot_id BIGINT NOT NULL,
    from_date TIMESTAMP NOT NULL,
    to_date TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (plane_id, pilot_id, from_date),
    FOREIGN KEY (plane_id) REFERENCES plane_details(plane_id),
    FOREIGN KEY (pilot_id) REFERENCES pilot_details(pilot_id),
    FOREIGN KEY (user_id) REFERENCES user_details(user_id)
);

-- =========================
-- Table: hangar_allocation
-- =========================
CREATE TABLE hangar_allocation (
    plane_id BIGINT NOT NULL,
    hangar_id BIGINT NOT NULL,
    from_date TIMESTAMP NOT NULL,
    to_date TIMESTAMP NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (plane_id, hangar_id, from_date),
    FOREIGN KEY (plane_id) REFERENCES plane_details(plane_id),
    FOREIGN KEY (hangar_id) REFERENCES hangar_details(hangar_id),
    FOREIGN KEY (user_id) REFERENCES user_details(user_id)
);
