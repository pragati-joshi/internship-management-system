package com.placement.model;

public class User {
    private String username;
    private String role;
    private String token;

    public User(String username, String role, String token) {
        this.username = username;
        this.role = role;
        this.token = token;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}
