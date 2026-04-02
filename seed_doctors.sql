-- Seed data for Hospital Management System

-- 1. Seed Users (user_db)
-- All passwords are 'Bhagya@123' (BCrypt hash example)
USE user_db;

INSERT IGNORE INTO users (id, email, password, role, active, is_verified) VALUES
(10, 'dr.smith@omnihealth.com', '$2a$10$vI8q8vH8W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9', 'DOCTOR', true, true),
(11, 'dr.johnson@omnihealth.com', '$2a$10$vI8q8vH8W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9', 'DOCTOR', true, true),
(12, 'dr.davis@omnihealth.com', '$2a$10$vI8q8vH8W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9', 'DOCTOR', true, true),
(13, 'dr.chen@omnihealth.com', '$2a$10$vI8q8vH8W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9', 'DOCTOR', true, true),
(14, 'dr.wilson@omnihealth.com', '$2a$10$vI8q8vH8W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9W9', 'DOCTOR', true, true);

-- 2. Seed Doctor Profiles (doctor_management_db)
USE doctor_management_db;

INSERT IGNORE INTO doctor_profiles (id, user_id, first_name, last_name, email, specialization, status, consultation_fee, is_available_for_telemedicine) VALUES
(1, 10, 'John', 'Smith', 'dr.smith@omnihealth.com', 'Cardiology', 'ACTIVE', 150.0, true),
(2, 11, 'Sarah', 'Johnson', 'dr.johnson@omnihealth.com', 'Neurology', 'ACTIVE', 200.0, true),
(3, 12, 'Emily', 'Davis', 'dr.davis@omnihealth.com', 'Pediatrics', 'ACTIVE', 120.0, true),
(4, 13, 'Michael', 'Chen', 'dr.chen@omnihealth.com', 'Orthopedics', 'ACTIVE', 180.0, true),
(5, 14, 'Robert', 'Wilson', 'dr.wilson@omnihealth.com', 'General Medicine', 'ACTIVE', 100.0, true);
