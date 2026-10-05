package com.placement.service;

import com.placement.app.UserContext;
import com.placement.model.User;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdminService {
    private static final String API_URL = "http://localhost:8080/internship-backend/api/users";
    private final HttpClient httpClient;

    public AdminService() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    }

    public CompletableFuture<List<User>> getAllUsers() {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET();

        User currentUser = UserContext.getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            builder.header("Authorization", "Bearer " + currentUser.getToken());
        }

        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) return getMockUsers();
                    throw new RuntimeException("Failed");
                })
                .exceptionally(ex -> {
                    System.err.println("Backend offline, returning mock users.");
                    return getMockUsers();
                });
    }

    private List<User> getMockUsers() {
        List<User> list = new ArrayList<>();
        list.add(new User("john_doe", "Student", ""));
        list.add(new User("jane_smith", "Mentor", ""));
        list.add(new User("admin_super", "Administrator", ""));
        return list;
    }
}
