package com.college.internship.servlet;

import com.college.internship.dao.EvaluationDAO;
import com.college.internship.model.Evaluation;
import com.college.internship.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/evaluations/*")
public class EvaluationServlet extends HttpServlet {

    private final EvaluationDAO evaluationDAO = new EvaluationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if (path != null && path.startsWith("/student/")) {
                int studentId = Integer.parseInt(path.substring("/student/".length()));
                List<Evaluation> list = evaluationDAO.getEvaluationsByStudent(studentId);
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Student evaluations retrieved", list);
            } else if (path != null && !"/".equals(path)) {
                int id = Integer.parseInt(path.substring(1));
                Evaluation eval = evaluationDAO.getEvaluationById(id);
                if (eval != null) {
                    JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Evaluation retrieved", eval);
                } else {
                    JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Evaluation not found");
                }
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Evaluation ID or Student ID path required");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching evaluations: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        Integer mentorId = (Integer) req.getAttribute("authenticatedUserId");

        if (!"MENTOR".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only Mentors can submit performance evaluations");
            return;
        }

        try {
            Evaluation eval = JsonUtil.parseJson(req, Evaluation.class);
            eval.setMentorId(mentorId);

            if (eval.getStudentId() == null || eval.getRating() == null || eval.getFeedback() == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Student ID, Rating (1-5), and Feedback text required");
                return;
            }

            Evaluation created = evaluationDAO.createEvaluation(eval);
            if (created != null) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_CREATED, "Evaluation submitted successfully", created);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to submit evaluation");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Evaluation submission error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer mentorId = (Integer) req.getAttribute("authenticatedUserId");
        String path = req.getPathInfo();

        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Evaluation ID required");
            return;
        }

        try {
            int id = Integer.parseInt(path.substring(1));
            Evaluation eval = JsonUtil.parseJson(req, Evaluation.class);
            eval.setEvaluationId(id);
            eval.setMentorId(mentorId);

            boolean updated = evaluationDAO.updateEvaluation(eval);
            if (updated) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Evaluation updated", eval);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Evaluation not found or unauthorized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating evaluation: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can delete evaluations");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Evaluation ID required");
            return;
        }

        try {
            int id = Integer.parseInt(path.substring(1));
            boolean deleted = evaluationDAO.deleteEvaluation(id);
            if (deleted) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Evaluation deleted successfully", null);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Evaluation not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting evaluation: " + e.getMessage());
        }
    }
}
