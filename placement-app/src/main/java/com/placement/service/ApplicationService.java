package com.placement.service;

import com.placement.app.UserContext;
import com.placement.model.User;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApplicationService {
    private static final String API_URL = "http://localhost:8080/internship-backend/api/applications";
    private final HttpClient httpClient;

    public ApplicationService() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    }

    public CompletableFuture<Boolean> applyForInternship(String internshipId) {
        String jsonBody = String.format("{\"internshipId\":%s}", internshipId);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        User currentUser = UserContext.getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            builder.header("Authorization", "Bearer " + currentUser.getToken());
        }

        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200 || response.statusCode() == 201)
                .exceptionally(ex -> {
                    System.err.println("Backend offline, mock successful application.");
                    return true;
                });
    }
}
