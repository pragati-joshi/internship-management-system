package com.college.internship.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> T parseJson(HttpServletRequest request, Class<T> clazz) throws IOException {
        return mapper.readValue(request.getInputStream(), clazz);
    }

    public static void sendJsonResponse(HttpServletResponse response, int statusCode, boolean success, String message, Object data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        if (data != null) {
            result.put("data", data);
        }

        mapper.writeValue(response.getWriter(), result);
    }

    public static void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        sendJsonResponse(response, statusCode, false, message, null);
    }

    public static void sendSuccess(HttpServletResponse response, int statusCode, String message, Object data) throws IOException {
        sendJsonResponse(response, statusCode, true, message, data);
    }
}
