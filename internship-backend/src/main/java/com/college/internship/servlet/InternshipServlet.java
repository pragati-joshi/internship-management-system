package com.college.internship.servlet;

import com.college.internship.dao.InternshipDAO;
import com.college.internship.model.Internship;
import com.college.internship.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/internships/*")
public class InternshipServlet extends HttpServlet {

    private final InternshipDAO internshipDAO = new InternshipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if (path == null || "/".equals(path)) {
                List<Internship> list = internshipDAO.getAllInternships();
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Internships retrieved", list);
            } else {
                int id = Integer.parseInt(path.substring(1));
                Internship internship = internshipDAO.getInternshipById(id);
                if (internship != null) {
                    JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Internship retrieved", internship);
                } else {
                    JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Internship not found");
                }
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid internship ID");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching internships: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can create internships");
            return;
        }

        try {
            Internship internship = JsonUtil.parseJson(req, Internship.class);
            if (internship.getCompanyName() == null || internship.getTitle() == null || internship.getDeadline() == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Company name, title, and deadline are required");
                return;
            }

            Internship created = internshipDAO.createInternship(internship);
            if (created != null) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_CREATED, "Internship posted successfully", created);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create internship");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating internship: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can update internships");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Internship ID required in URL path");
            return;
        }

        try {
            int id = Integer.parseInt(path.substring(1));
            Internship internship = JsonUtil.parseJson(req, Internship.class);
            internship.setInternshipId(id);

            boolean updated = internshipDAO.updateInternship(internship);
            if (updated) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Internship updated successfully", internship);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Internship not found or update failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating internship: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can delete internships");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Internship ID required");
            return;
        }

        try {
            int id = Integer.parseInt(path.substring(1));
            boolean deleted = internshipDAO.deleteInternship(id);
            if (deleted) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Internship deleted successfully", null);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Internship not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting internship: " + e.getMessage());
        }
    }
}
