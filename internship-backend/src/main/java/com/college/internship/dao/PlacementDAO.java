package com.college.internship.dao;

import com.college.internship.model.Placement;
import com.college.internship.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlacementDAO {

    public Placement createPlacement(Placement placement) throws SQLException {
        String sql = "INSERT INTO placements (student_id, company_name, package, joining_date, status) VALUES (?, ?, ?, ?, ?) RETURNING placement_id, created_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, placement.getStudentId());
            pstmt.setString(2, placement.getCompanyName());
            pstmt.setDouble(3, placement.getPackageAmount());
            pstmt.setDate(4, placement.getJoiningDate());
            pstmt.setString(5, placement.getStatus() != null ? placement.getStatus() : "ACCEPTED");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    placement.setPlacementId(rs.getInt("placement_id"));
                    placement.setCreatedAt(rs.getTimestamp("created_at"));
                    return placement;
                }
            }
        }
        return null;
    }

    public Placement getPlacementById(int id) throws SQLException {
        String sql = "SELECT p.*, u.name as student_name FROM placements p " +
                     "JOIN students s ON p.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id WHERE p.placement_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapPlacement(rs);
                }
            }
        }
        return null;
    }

    public List<Placement> getPlacementsByStudent(int studentId) throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = "SELECT p.*, u.name as student_name FROM placements p " +
                     "JOIN students s ON p.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id WHERE p.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPlacement(rs));
                }
            }
        }
        return list;
    }

    public List<Placement> getAllPlacements() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = "SELECT p.*, u.name as student_name FROM placements p " +
                     "JOIN students s ON p.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id ORDER BY p.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapPlacement(rs));
            }
        }
        return list;
    }

    public boolean updatePlacement(Placement placement) throws SQLException {
        String sql = "UPDATE placements SET company_name = ?, package = ?, joining_date = ?, status = ? WHERE placement_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, placement.getCompanyName());
            pstmt.setDouble(2, placement.getPackageAmount());
            pstmt.setDate(3, placement.getJoiningDate());
            pstmt.setString(4, placement.getStatus());
            pstmt.setInt(5, placement.getPlacementId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deletePlacement(int id) throws SQLException {
        String sql = "DELETE FROM placements WHERE placement_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Placement mapPlacement(ResultSet rs) throws SQLException {
        Placement p = new Placement();
        p.setPlacementId(rs.getInt("placement_id"));
        p.setStudentId(rs.getInt("student_id"));
        p.setCompanyName(rs.getString("company_name"));
        p.setPackageAmount(rs.getDouble("package"));
        p.setJoiningDate(rs.getDate("joining_date"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setStudentName(rs.getString("student_name"));
        return p;
    }
}
