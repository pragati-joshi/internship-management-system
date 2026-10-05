package com.college.internship.servlet;

import com.college.internship.dao.PlacementDAO;
import com.college.internship.model.Placement;
import com.college.internship.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/placements/*")
public class PlacementServlet extends HttpServlet {

    private final PlacementDAO placementDAO = new PlacementDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if (path == null || "/".equals(path)) {
                List<Placement> list = placementDAO.getAllPlacements();
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Placements retrieved", list);
            } else if (path.startsWith("/student/")) {
                int studentId = Integer.parseInt(path.substring("/student/".length()));
                List<Placement> list = placementDAO.getPlacementsByStudent(studentId);
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Student placements retrieved", list);
            } else {
                int id = Integer.parseInt(path.substring(1));
                Placement placement = placementDAO.getPlacementById(id);
                if (placement != null) {
                    JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Placement retrieved", placement);
                } else {
                    JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Placement record not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching placements: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can add placement records");
            return;
        }

        try {
            Placement placement = JsonUtil.parseJson(req, Placement.class);
            if (placement.getStudentId() == null || placement.getCompanyName() == null || placement.getPackageAmount() == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Student ID, Company Name, and Package Amount required");
                return;
            }

            Placement created = placementDAO.createPlacement(placement);
            if (created != null) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_CREATED, "Placement record created successfully", created);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create placement record");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating placement: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can modify placement records");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Placement ID required in path");
            return;
        }

        try {
            int id = Integer.parseInt(path.substring(1));
            Placement placement = JsonUtil.parseJson(req, Placement.class);
            placement.setPlacementId(id);

            boolean updated = placementDAO.updatePlacement(placement);
            if (updated) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Placement record updated", placement);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Placement record not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating placement: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only ADMIN can delete placement records");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Placement ID required");
            return;
        }

        try {
            int id = Integer.parseInt(path.substring(1));
            boolean deleted = placementDAO.deletePlacement(id);
            if (deleted) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Placement record deleted successfully", null);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Placement record not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting placement: " + e.getMessage());
        }
    }
}
