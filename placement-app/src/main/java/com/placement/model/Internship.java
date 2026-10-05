package com.placement.model;

public class Internship {
    private String id;
    private String companyName;
    private String title;
    private String status;

    public Internship(String id, String companyName, String title, String status) {
        this.id = id;
        this.companyName = companyName;
        this.title = title;
        this.status = status;
    }

    public String getId() { return id; }
    public String getCompanyName() { return companyName; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
}
