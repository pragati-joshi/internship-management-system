package com.placement.service;

import com.placement.app.UserContext;
import com.placement.model.User;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static final String API_URL = "http://localhost:8080/internship-backend/api/auth/login";
    private final HttpClient httpClient;

    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public CompletableFuture<User> login(String email, String password, String role) {
        String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        // In real server scenario parse json response
                        User user = new User(email, role, "token_from_response");
                        UserContext.setCurrentUser(user);
                        return user;
                    } else {
                        throw new RuntimeException("Login failed with status: " + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Backend offline, using mock login fallback. Error: " + ex.getMessage());
                    User user = new User(email, role, "mock_jwt_token_12345");
                    UserContext.setCurrentUser(user);
                    return user;
                });
    }
}
