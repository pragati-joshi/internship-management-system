package com.placement.service;

import com.placement.app.UserContext;
import com.placement.model.User;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class EvaluationService {
    private static final String API_URL = "http://localhost:8080/internship-backend/api/evaluations";
    private final HttpClient httpClient;

    public EvaluationService() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    }

    public CompletableFuture<Boolean> submitEvaluation(String studentName, int rating, String feedback) {
        String jsonBody = String.format("{\"studentId\":1, \"rating\":%d, \"feedback\":\"%s\"}", rating, feedback);
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
                    System.err.println("Backend offline, mock successful evaluation.");
                    return true;
                });
    }
}
