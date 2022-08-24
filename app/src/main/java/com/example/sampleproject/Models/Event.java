package com.example.sampleproject.Models;

public class Event {

    private String title;
    private String description;
    private String deadline;
    private String daysleft;

    public Event() {}

    public Event(String title, String description, String deadline, String daysleft) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.daysleft = daysleft;
    }

    // Getter and setter method
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public  String getDaysLeft() { return daysleft; }
    public void setDaysLeft(String daysLeft) {this.daysleft = daysleft; }
}