package com.college.internship.model;

public class Student {
    private Integer studentId;
    private Integer userId;
    private String rollNumber;
    private String branch;
    private Integer year;
    private Double cgpa;
    private String phone;
    
    // Additional populated fields
    private String name;
    private String email;

    public Student() {}

    public Student(Integer studentId, Integer userId, String rollNumber, String branch, Integer year, Double cgpa, String phone) {
        this.studentId = studentId;
        this.userId = userId;
        this.rollNumber = rollNumber;
        this.branch = branch;
        this.year = year;
        this.cgpa = cgpa;
        this.phone = phone;
    }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
