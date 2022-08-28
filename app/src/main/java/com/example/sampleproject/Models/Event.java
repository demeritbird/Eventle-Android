package com.example.sampleproject.Models;

public class Event {

    private String title;
    private String description;
    private String deadline;
    private String daysleft;
    private String uid;
    private Boolean isprivate;

    public Event() {}


    // FIXME: Change to int type for daysleft --> requires a helper function i guess
    public Event(String title, String description, String deadline, String daysleft, String uid, Boolean isprivate) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.daysleft = daysleft;
        this.uid = uid;
        this.isprivate = isprivate;
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
    public  String getUid() { return uid; }
    public void setUid(String uid) {this.uid = uid; }
    public  Boolean getIsPrivate() { return isprivate; }
    public void setIsPrivate() {this.isprivate = isprivate; }




}