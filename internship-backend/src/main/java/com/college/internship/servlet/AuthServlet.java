package com.college.internship.servlet;

import com.college.internship.dao.StudentDAO;
import com.college.internship.dao.UserDAO;
import com.college.internship.model.Student;
import com.college.internship.model.User;
import com.college.internship.util.JsonUtil;
import com.college.internship.util.JwtUtil;
import com.college.internship.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else {
            JsonUtil.sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Auth endpoint not found");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, Object> body = JsonUtil.parseJson(req, Map.class);
            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            String role = (String) body.get("role");

            if (email == null || password == null || name == null || role == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields: name, email, password, role");
                return;
            }

            if (userDAO.getUserByEmail(email) != null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_CONFLICT, "User with this email already exists");
                return;
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRole(role.toUpperCase());

            User createdUser = userDAO.createUser(user);
            if (createdUser == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to register user");
                return;
            }

            // If role is STUDENT, populate student table details
            if ("STUDENT".equalsIgnoreCase(role)) {
                Student student = new Student();
                student.setUserId(createdUser.getUserId());
                student.setRollNumber((String) body.getOrDefault("rollNumber", "TEMP-" + System.currentTimeMillis()));
                student.setBranch((String) body.getOrDefault("branch", "General"));
                student.setYear(((Number) body.getOrDefault("year", 1)).intValue());
                student.setCgpa(((Number) body.getOrDefault("cgpa", 0.0)).doubleValue());
                student.setPhone((String) body.getOrDefault("phone", "0000000000"));
                studentDAO.createStudent(student);
            }

            String token = JwtUtil.generateToken(createdUser.getUserId(), createdUser.getEmail(), createdUser.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("userId", createdUser.getUserId());
            data.put("name", createdUser.getName());
            data.put("email", createdUser.getEmail());
            data.put("role", createdUser.getRole());
            data.put("token", token);

            JsonUtil.sendSuccess(resp, HttpServletResponse.SC_CREATED, "User registered successfully", data);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, Object> body = JsonUtil.parseJson(req, Map.class);
            String email = (String) body.get("email");
            String password = (String) body.get("password");

            if (email == null || password == null) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Email and password are required");
                return;
            }

            User user = userDAO.getUserByEmail(email);
            if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                JsonUtil.sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
                return;
            }

            String token = JwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            data.put("name", user.getName());
            data.put("email", user.getEmail());
            data.put("role", user.getRole());
            data.put("token", token);

            // Fetch student ID if student
            if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                Student s = studentDAO.getStudentByUserId(user.getUserId());
                if (s != null) {
                    data.put("studentId", s.getStudentId());
                }
            }

            JsonUtil.sendSuccess(resp, HttpServletResponse.SC_OK, "Login successful", data);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login error: " + e.getMessage());
        }
    }
}
