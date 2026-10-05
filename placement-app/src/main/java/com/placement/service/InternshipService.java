package com.placement.service;

import com.placement.app.UserContext;
import com.placement.model.Internship;
import com.placement.model.User;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InternshipService {
    private static final String API_URL = "http://localhost:8080/internship-backend/api/internships";
    private final HttpClient httpClient;

    public InternshipService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public CompletableFuture<List<Internship>> getAvailableInternships() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET();

        User currentUser = UserContext.getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            builder.header("Authorization", "Bearer " + currentUser.getToken());
        }

        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return getMockInternships();
                    } else {
                        throw new RuntimeException("Failed to fetch: " + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Backend offline, returning mock internship data.");
                    return getMockInternships();
                });
    }

    private List<Internship> getMockInternships() {
        List<Internship> list = new ArrayList<>();
        list.add(new Internship("1", "Tech Corp", "Software Engineering Intern", "Open"));
        list.add(new Internship("2", "DataWorks", "Data Science Intern", "Open"));
        list.add(new Internship("3", "CloudNet", "DevOps Intern", "Closing Soon"));
        return list;
    }
}
