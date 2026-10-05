package com.college.internship.dao;

import com.college.internship.model.Application;
import com.college.internship.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public Application createApplication(Application app) throws SQLException {
        String sql = "INSERT INTO applications (student_id, internship_id, status) VALUES (?, ?, ?) RETURNING application_id, applied_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, app.getStudentId());
            pstmt.setInt(2, app.getInternshipId());
            pstmt.setString(3, app.getStatus() != null ? app.getStatus() : "PENDING");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    app.setApplicationId(rs.getInt("application_id"));
                    app.setAppliedAt(rs.getTimestamp("applied_at"));
                    return app;
                }
            }
        }
        return null;
    }

    public boolean hasAlreadyApplied(int studentId, int internshipId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications WHERE student_id = ? AND internship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, internshipId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Application getApplicationById(int id) throws SQLException {
        String sql = "SELECT a.*, u.name as student_name, i.company_name, i.title as internship_title " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN internships i ON a.internship_id = i.internship_id " +
                     "WHERE a.application_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapApplication(rs);
                }
            }
        }
        return null;
    }

    public List<Application> getApplicationsByStudent(int studentId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name as student_name, i.company_name, i.title as internship_title " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN internships i ON a.internship_id = i.internship_id " +
                     "WHERE a.student_id = ? ORDER BY a.applied_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapApplication(rs));
                }
            }
        }
        return list;
    }

    public List<Application> getApplicationsByInternship(int internshipId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name as student_name, i.company_name, i.title as internship_title " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN internships i ON a.internship_id = i.internship_id " +
                     "WHERE a.internship_id = ? ORDER BY a.applied_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, internshipId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapApplication(rs));
                }
            }
        }
        return list;
    }

    public boolean updateApplicationStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, applicationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean withdrawApplication(int applicationId, int studentId) throws SQLException {
        String sql = "UPDATE applications SET status = 'WITHDRAWN' WHERE application_id = ? AND student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, applicationId);
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Application mapApplication(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setApplicationId(rs.getInt("application_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setInternshipId(rs.getInt("internship_id"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        a.setStatus(rs.getString("status"));
        a.setStudentName(rs.getString("student_name"));
        a.setCompanyName(rs.getString("company_name"));
        a.setInternshipTitle(rs.getString("internship_title"));
        return a;
    }
}
