package com.placement.model;

public class Application {
    private String id;
    private String internshipTitle;
    private String company;
    private String status;

    public Application(String id, String internshipTitle, String company, String status) {
        this.id = id;
        this.internshipTitle = internshipTitle;
        this.company = company;
        this.status = status;
    }

    public String getId() { return id; }
    public String getInternshipTitle() { return internshipTitle; }
    public String getCompany() { return company; }
    public String getStatus() { return status; }
}
