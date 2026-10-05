-- PostgreSQL Schema for Internship & Placement Management System
-- Database Name: internship_management

-- Drop tables if exists (in reverse dependency order)
DROP TABLE IF EXISTS evaluations CASCADE;
DROP TABLE IF EXISTS placements CASCADE;
DROP TABLE IF EXISTS applications CASCADE;
DROP TABLE IF EXISTS internships CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. USERS TABLE
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'MENTOR', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index on email for fast authentication lookups
CREATE INDEX idx_users_email ON users(email);

-- 2. STUDENTS TABLE
CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    roll_number VARCHAR(50) UNIQUE NOT NULL,
    branch VARCHAR(100) NOT NULL,
    year INT NOT NULL CHECK (year >= 1 AND year <= 5),
    cgpa NUMERIC(4, 2) NOT NULL CHECK (cgpa >= 0.0 AND cgpa <= 10.0),
    phone VARCHAR(20) NOT NULL
);

-- Index on roll number
CREATE INDEX idx_students_roll ON students(roll_number);

-- 3. INTERNSHIPS TABLE
CREATE TABLE internships (
    internship_id SERIAL PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100) NOT NULL,
    stipend NUMERIC(10, 2) NOT NULL DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    deadline DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index on company name and title
CREATE INDEX idx_internships_company ON internships(company_name);

-- 4. APPLICATIONS TABLE
CREATE TABLE applications (
    application_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    internship_id INT NOT NULL REFERENCES internships(internship_id) ON DELETE CASCADE,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
        CHECK (status IN ('PENDING', 'SHORTLISTED', 'REJECTED', 'SELECTED', 'WITHDRAWN')),
    CONSTRAINT unique_student_internship UNIQUE (student_id, internship_id)
);

-- Index on student and internship FKs
CREATE INDEX idx_applications_student ON applications(student_id);
CREATE INDEX idx_applications_internship ON applications(internship_id);

-- 5. PLACEMENTS TABLE
CREATE TABLE placements (
    placement_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    company_name VARCHAR(150) NOT NULL,
    package NUMERIC(12, 2) NOT NULL,
    joining_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACCEPTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_placements_student ON placements(student_id);

-- 6. EVALUATIONS TABLE
CREATE TABLE evaluations (
    evaluation_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    mentor_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    feedback TEXT NOT NULL,
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_evaluations_student ON evaluations(student_id);

-- SAMPLE SEED DATA
-- Passwords below are BCrypt hash of "password123": $2a$10$w6YJ2Bw2yJ4K2E5.n7D2t.M/G1E0v9iYv.Z7Z4n5b8.J5z4q2Z4qW
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin User', 'admin@college.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tqO050zWf5Vq9S', 'ADMIN'),
('Dr. Robert Vance', 'mentor@college.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tqO050zWf5Vq9S', 'MENTOR'),
('John Doe', 'john@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tqO050zWf5Vq9S', 'STUDENT');

INSERT INTO students (user_id, roll_number, branch, year, cgpa, phone) VALUES
(3, 'CS101', 'Computer Science', 3, 8.50, '9876543210');

INSERT INTO internships (company_name, title, description, location, stipend, start_date, end_date, deadline) VALUES
('Tech Corp', 'Software Engineering Intern', 'Develop Java backend web services', 'Remote', 25000.00, '2026-06-01', '2026-08-31', '2026-05-15'),
('DataWorks', 'Data Analyst Intern', 'Build SQL analytics dashboards', 'New York, NY', 30000.00, '2026-06-15', '2026-09-15', '2026-05-30');

INSERT INTO applications (student_id, internship_id, status) VALUES
(1, 1, 'PENDING');
