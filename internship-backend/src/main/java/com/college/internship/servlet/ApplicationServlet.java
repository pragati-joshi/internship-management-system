package com.college.internship.servlet;

import com.college.internship.dao.ApplicationDAO;
import com.college.internship.dao.InternshipDAO;
import com.college.internship.dao.StudentDAO;
import com.college.internship.model.Application;
import com.college.internship.model.Internship;
import com.college.internship.model.Student;
import com.college.internship.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/api/applications/*")
public class ApplicationServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InternshipDAO internshipDAO = new InternshipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if (path == null || "/".equals(path)) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Application ID or query criteria required");
                return;
            }

            if (path.startsWith("/student/")) {
                int studentId = Integer.parseInt(path.substring("/student/".length()));
                List<Application> list = applicationDAO.getApplicationsByStudent(studentId);
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Student applications retrieved", list);
            } else if (path.startsWith("/internship/")) {
                int internshipId = Integer.parseInt(path.substring("/internship/".length()));
                List<Application> list = applicationDAO.getApplicationsByInternship(internshipId);
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Internship applications retrieved", list);
            } else {
                int appId = Integer.parseInt(path.substring(1));
                Application app = applicationDAO.getApplicationById(appId);
                if (app != null) {
                    JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Application retrieved", app);
                } else {
                    JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Application not found");
                }
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching applications: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer authUserId = (Integer) req.getAttribute("authenticatedUserId");
        String role = (String) req.getAttribute("authenticatedRole");

        try {
            Map<String, Object> body = JsonUtil.parseJson(req, Map.class);
            Integer internshipId = ((Number) body.get("internshipId")).intValue();

            // Resolve student ID
            Student student = studentDAO.getStudentByUserId(authUserId);
            if (student == null && "STUDENT".equalsIgnoreCase(role)) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Student profile not found for user");
                return;
            }

            int studentId = (student != null) ? student.getStudentId() : ((Number) body.getOrDefault("studentId", 0)).intValue();

            // 1. Verify internship exists
            Internship internship = internshipDAO.getInternshipById(internshipId);
            if (internship == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Internship does not exist");
                return;
            }

            // 2. Verify deadline has not passed
            Date today = new Date(System.currentTimeMillis());
            if (internship.getDeadline() != null && internship.getDeadline().before(today)) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Application deadline has already passed");
                return;
            }

            // 3. Verify student hasn't applied twice
            if (applicationDAO.hasAlreadyApplied(studentId, internshipId)) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_CONFLICT, "You have already applied for this internship");
                return;
            }

            Application app = new Application();
            app.setStudentId(studentId);
            app.setInternshipId(internshipId);
            app.setStatus("PENDING");

            Application created = applicationDAO.createApplication(app);
            if (created != null) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_CREATED, "Application submitted successfully", created);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to submit application");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Application error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role) && !"MENTOR".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only Admin or Mentor can update application status");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || !path.endsWith("/status")) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Expected format: /api/applications/{id}/status");
            return;
        }

        try {
            String idStr = path.substring(1, path.indexOf("/status"));
            int appId = Integer.parseInt(idStr);

            Map<String, Object> body = JsonUtil.parseJson(req, Map.class);
            String newStatus = (String) body.get("status");

            if (newStatus == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Status field is required");
                return;
            }

            boolean updated = applicationDAO.updateApplicationStatus(appId, newStatus.toUpperCase());
            if (updated) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Application status updated to " + newStatus, null);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Application not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating status: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer authUserId = (Integer) req.getAttribute("authenticatedUserId");
        String path = req.getPathInfo();

        if (path == null || "/".equals(path)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Application ID required");
            return;
        }

        try {
            int appId = Integer.parseInt(path.substring(1));
            Student student = studentDAO.getStudentByUserId(authUserId);
            
            if (student == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only student owners can withdraw applications");
                return;
            }

            boolean withdrawn = applicationDAO.withdrawApplication(appId, student.getStudentId());
            if (withdrawn) {
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Application withdrawn successfully", null);
            } else {
                JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Application not found or unauthorized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error withdrawing application: " + e.getMessage());
        }
    }
}
