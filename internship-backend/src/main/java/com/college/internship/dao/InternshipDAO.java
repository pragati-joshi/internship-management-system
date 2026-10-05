package com.college.internship.dao;

import com.college.internship.model.Internship;
import com.college.internship.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InternshipDAO {

    public Internship createInternship(Internship internship) throws SQLException {
        String sql = "INSERT INTO internships (company_name, title, description, location, stipend, start_date, end_date, deadline) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING internship_id, created_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, internship.getCompanyName());
            pstmt.setString(2, internship.getTitle());
            pstmt.setString(3, internship.getDescription());
            pstmt.setString(4, internship.getLocation());
            pstmt.setDouble(5, internship.getStipend() != null ? internship.getStipend() : 0.0);
            pstmt.setDate(6, internship.getStartDate());
            pstmt.setDate(7, internship.getEndDate());
            pstmt.setDate(8, internship.getDeadline());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    internship.setInternshipId(rs.getInt("internship_id"));
                    internship.setCreatedAt(rs.getTimestamp("created_at"));
                    return internship;
                }
            }
        }
        return null;
    }

    public Internship getInternshipById(int id) throws SQLException {
        String sql = "SELECT * FROM internships WHERE internship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapInternship(rs);
                }
            }
        }
        return null;
    }

    public List<Internship> getAllInternships() throws SQLException {
        List<Internship> list = new ArrayList<>();
        String sql = "SELECT * FROM internships ORDER BY deadline ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapInternship(rs));
            }
        }
        return list;
    }

    public boolean updateInternship(Internship internship) throws SQLException {
        String sql = "UPDATE internships SET company_name = ?, title = ?, description = ?, location = ?, stipend = ?, " +
                     "start_date = ?, end_date = ?, deadline = ? WHERE internship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, internship.getCompanyName());
            pstmt.setString(2, internship.getTitle());
            pstmt.setString(3, internship.getDescription());
            pstmt.setString(4, internship.getLocation());
            pstmt.setDouble(5, internship.getStipend());
            pstmt.setDate(6, internship.getStartDate());
            pstmt.setDate(7, internship.getEndDate());
            pstmt.setDate(8, internship.getDeadline());
            pstmt.setInt(9, internship.getInternshipId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteInternship(int id) throws SQLException {
        String sql = "DELETE FROM internships WHERE internship_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Internship mapInternship(ResultSet rs) throws SQLException {
        Internship i = new Internship();
        i.setInternshipId(rs.getInt("internship_id"));
        i.setCompanyName(rs.getString("company_name"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setLocation(rs.getString("location"));
        i.setStipend(rs.getDouble("stipend"));
        i.setStartDate(rs.getDate("start_date"));
        i.setEndDate(rs.getDate("end_date"));
        i.setDeadline(rs.getDate("deadline"));
        i.setCreatedAt(rs.getTimestamp("created_at"));
        return i;
    }
}
