package com.college.internship.dao;

import com.college.internship.model.Evaluation;
import com.college.internship.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO {

    public Evaluation createEvaluation(Evaluation eval) throws SQLException {
        String sql = "INSERT INTO evaluations (student_id, mentor_id, rating, feedback) VALUES (?, ?, ?, ?) RETURNING evaluation_id, evaluated_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eval.getStudentId());
            pstmt.setInt(2, eval.getMentorId());
            pstmt.setInt(3, eval.getRating());
            pstmt.setString(4, eval.getFeedback());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    eval.setEvaluationId(rs.getInt("evaluation_id"));
                    eval.setEvaluatedAt(rs.getTimestamp("evaluated_at"));
                    return eval;
                }
            }
        }
        return null;
    }

    public Evaluation getEvaluationById(int id) throws SQLException {
        String sql = "SELECT e.*, su.name as student_name, mu.name as mentor_name " +
                     "FROM evaluations e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN users su ON s.user_id = su.user_id " +
                     "JOIN users mu ON e.mentor_id = mu.user_id WHERE e.evaluation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapEvaluation(rs);
                }
            }
        }
        return null;
    }

    public List<Evaluation> getEvaluationsByStudent(int studentId) throws SQLException {
        List<Evaluation> list = new ArrayList<>();
        String sql = "SELECT e.*, su.name as student_name, mu.name as mentor_name " +
                     "FROM evaluations e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN users su ON s.user_id = su.user_id " +
                     "JOIN users mu ON e.mentor_id = mu.user_id WHERE e.student_id = ? ORDER BY e.evaluated_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvaluation(rs));
                }
            }
        }
        return list;
    }

    public boolean updateEvaluation(Evaluation eval) throws SQLException {
        String sql = "UPDATE evaluations SET rating = ?, feedback = ? WHERE evaluation_id = ? AND mentor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eval.getRating());
            pstmt.setString(2, eval.getFeedback());
            pstmt.setInt(3, eval.getEvaluationId());
            pstmt.setInt(4, eval.getMentorId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteEvaluation(int id) throws SQLException {
        String sql = "DELETE FROM evaluations WHERE evaluation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Evaluation mapEvaluation(ResultSet rs) throws SQLException {
        Evaluation e = new Evaluation();
        e.setEvaluationId(rs.getInt("evaluation_id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setMentorId(rs.getInt("mentor_id"));
        e.setRating(rs.getInt("rating"));
        e.setFeedback(rs.getString("feedback"));
        e.setEvaluatedAt(rs.getTimestamp("evaluated_at"));
        e.setStudentName(rs.getString("student_name"));
        e.setMentorName(rs.getString("mentor_name"));
        return e;
    }
}
