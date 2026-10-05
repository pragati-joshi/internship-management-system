package com.college.internship.dao;

import com.college.internship.model.Student;
import com.college.internship.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (user_id, roll_number, branch, year, cgpa, phone) VALUES (?, ?, ?, ?, ?, ?) RETURNING student_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getRollNumber());
            pstmt.setString(3, student.getBranch());
            pstmt.setInt(4, student.getYear());
            pstmt.setDouble(5, student.getCgpa());
            pstmt.setString(6, student.getPhone());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    student.setStudentId(rs.getInt("student_id"));
                    return student;
                }
            }
        }
        return null;
    }

    public Student getStudentByUserId(int userId) throws SQLException {
        String sql = "SELECT s.*, u.name, u.email FROM students s JOIN users u ON s.user_id = u.user_id WHERE s.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT s.*, u.name, u.email FROM students s JOIN users u ON s.user_id = u.user_id WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.name, u.email FROM students s JOIN users u ON s.user_id = u.user_id ORDER BY s.student_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        }
        return list;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student(
                rs.getInt("student_id"),
                rs.getInt("user_id"),
                rs.getString("roll_number"),
                rs.getString("branch"),
                rs.getInt("year"),
                rs.getDouble("cgpa"),
                rs.getString("phone")
        );
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        return s;
    }
}
