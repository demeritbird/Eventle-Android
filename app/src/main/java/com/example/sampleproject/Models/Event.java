package com.example.sampleproject.Models;

public class Event {

    private String title;
    private String description;
    private String deadline;
    private Integer daysleft;
    private String uid;
    private Boolean isprivate;
    private Boolean iscomplete;

    public Event() {}


    // FIXME: Change to int type for daysleft --> requires a helper function i guess
    public Event(String title, String description, String deadline, Integer daysleft, String uid, Boolean isprivate, Boolean iscomplete) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.daysleft = daysleft;
        this.uid = uid;
        this.isprivate = isprivate;
        this.iscomplete = iscomplete;
    }

    // Getter and setter method
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public  Integer getDaysLeft() { return daysleft; }
    public void setDaysLeft(Integer daysLeft) {this.daysleft = daysleft; }
    public  String getUid() { return uid; }
    public void setUid(String uid) {this.uid = uid; }
    public  Boolean getIsPrivate() { return isprivate; }
    public void setIsPrivate() {this.isprivate = isprivate; }
    public  Boolean getIsComplete() { return iscomplete; }
    public void setIsComplete() {this.iscomplete = iscomplete; }




}