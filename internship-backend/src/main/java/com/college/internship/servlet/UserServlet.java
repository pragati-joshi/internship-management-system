package com.college.internship.servlet;

import com.college.internship.dao.StudentDAO;
import com.college.internship.dao.UserDAO;
import com.college.internship.model.Student;
import com.college.internship.model.User;
import com.college.internship.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = (String) req.getAttribute("authenticatedRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }

        String path = req.getPathInfo();
        try {
            if (path == null || "/".equals(path)) {
                List<User> users = userDAO.getAllUsers();
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Users retrieved successfully", users);
            } else if ("/students".equals(path)) {
                List<Student> students = studentDAO.getAllStudents();
                JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Students retrieved successfully", students);
            } else {
                int userId = Integer.parseInt(path.substring(1));
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "User retrieved", user);
                } else {
                    JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "User not found");
                }
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }
}
